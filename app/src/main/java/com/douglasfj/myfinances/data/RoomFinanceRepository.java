package com.douglasfj.myfinances.data;

import com.douglasfj.myfinances.constantes.*;
import com.douglasfj.myfinances.dao.FinanceiroDAO;
import com.douglasfj.myfinances.database.MyFinancesDatabase;
import com.douglasfj.myfinances.models.entities.*;
import com.douglasfj.myfinances.data.FinanceModels.State;
import java.io.File;
import java.io.IOException;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class RoomFinanceRepository implements FinanceRepository {
    private final MyFinancesDatabase db;
    private final FinanceiroDAO dao;
    public RoomFinanceRepository(MyFinancesDatabase db) { this.db = db; dao = db.financeiroDAO(); }
    public static int key(YearMonth month) { return month.getYear() * 12 + month.getMonthValue() - 1; }

    private MesesEntity ensureMonth(YearMonth month) {
        MesesEntity entity = dao.month(month.getMonthValue(), month.getYear());
        if (entity == null) {
            entity = new MesesEntity(month.getMonthValue(), month.getYear());
            entity.idMes = dao.insertMonth(entity);
        }
        materialize(entity);
        return entity;
    }
    private void materialize(MesesEntity month) {
        for (ValoresEntity value : dao.recurring(key(YearMonth.of(month.ano, month.mes)))) {
            if (dao.linkCount(month.idMes, value.idValor) == 0) link(month.idMes, value, false);
        }
    }
    private void link(long month, ValoresEntity value, boolean paid) {
        dao.insertLink(new MesesValoresEntity(month, value.idValor,
                value.indicadorTipoValor == IndicadorTipoValor.RECEITA ? null :
                        (paid ? IndicadorDespesaPaga.PAGA : IndicadorDespesaPaga.NAO_PAGA)));
    }
    @Override public State load(YearMonth month) {
        return db.runInTransaction(() -> {
            MesesEntity current = ensureMonth(month);
            List<MesesEntity> recorded = dao.months();
            Set<Integer> keys = new HashSet<>();
            for (MesesEntity item : recorded) keys.add(key(YearMonth.of(item.ano, item.mes)));
            YearMonth first = YearMonth.of(recorded.get(0).ano, recorded.get(0).mes);
            MesesEntity latest = recorded.get(recorded.size() - 1);
            YearMonth last = YearMonth.of(latest.ano, latest.mes);
            for (YearMonth item = first; !item.isAfter(last); item = item.plusMonths(1)) {
                if (!keys.contains(key(item))) ensureMonth(item);
            }
            for (MesesEntity existing : dao.months()) materialize(existing);
            return new State(month, dao.entries(current.idMes), dao.history(), dao.cards(), dao.purchases());
        });
    }
    private static String name(String name) {
        if (name == null || name.trim().isEmpty() || name.trim().length() > 120)
            throw new IllegalArgumentException("Informe um nome de 1 a 120 caracteres.");
        return name.trim();
    }
    private static void amount(long cents) {
        if (cents <= 0 || cents > 99999999999L) throw new IllegalArgumentException("Informe um valor positivo válido.");
    }
    @Override public void saveEntry(Long id, YearMonth month, String title, long cents,
                                    boolean income, boolean tax, boolean fixed, boolean paid) {
        String validName = name(title); amount(cents);
        db.runInTransaction(() -> {
            ValoresEntity old = id == null ? null : dao.value(id);
            if (id != null && old == null) throw new IllegalArgumentException("Lançamento não encontrado.");
            if (old != null && old.idCompraCartao != null)
                throw new IllegalArgumentException("Edite esta parcela na aba Cartões.");
            Integer previousEnd = old == null ? null : old.mesFimFixo;
            if (old != null) deleteEntryInternal(old, month);
            ValoresEntity value = new ValoresEntity(validName, cents / 100.0, new Date(),
                    fixed ? IndicadorValorFixo.FIXO : IndicadorValorFixo.NAO_FIXO,
                    income ? IndicadorTipoValor.RECEITA : IndicadorTipoValor.DESPESA, TipoDespesa.EXTRAS, null);
            value.imposto = !income && tax;
            value.mesInicioFixo = fixed ? key(month) : null;
            value.mesFimFixo = fixed ? previousEnd : null;
            value.idValor = dao.insertValue(value);
            MesesEntity target = ensureMonth(month);
            // ensureMonth may already have materialized this recurring entry.
            if (dao.linkCount(target.idMes, value.idValor) == 0) link(target.idMes, value, paid);
            else for (FinanceModels.Entry entry : dao.entries(target.idMes)) {
                if (entry.value.idValor.equals(value.idValor)) dao.setPaid(entry.linkId, paid ? 1 : 0);
            }
            for (MesesEntity existing : dao.months()) materialize(existing);
        });
    }
    private void deleteEntryInternal(ValoresEntity value, YearMonth month) {
        if (value.mesInicioFixo != null) {
            value.mesFimFixo = Math.min(value.mesFimFixo == null ? Integer.MAX_VALUE : value.mesFimFixo, key(month) - 1);
            dao.updateValue(value);
            dao.deleteLinksFrom(value.idValor, key(month));
        } else {
            dao.deleteLinks(value.idValor); dao.deleteValue(value.idValor);
        }
    }
    @Override public void deleteEntry(long id, YearMonth fromMonth) {
        db.runInTransaction(() -> {
            ValoresEntity value = dao.value(id);
            if (value == null) return;
            if (value.idCompraCartao != null) throw new IllegalArgumentException("Exclua a compra na aba Cartões.");
            deleteEntryInternal(value, fromMonth);
        });
    }
    @Override public void setPaid(long linkId, boolean paid) { dao.setPaid(linkId, paid ? 1 : 0); }
    @Override public void setInvoicePaid(long cardId, YearMonth month, boolean paid) {
        db.runInTransaction(() -> dao.setInvoicePaid(cardId,month.getMonthValue(),month.getYear(),paid ? 1 : 0));
    }
    @Override public LedgerPage ledger(boolean income, String search, YearMonth month, int offset) {
        return db.runInTransaction(() -> {
            List<LedgerRow> rows=dao.ledger(LedgerQuery.page(income,search,month,offset));
            boolean more=rows.size()>LedgerPage.SIZE;
            if(more) rows=new java.util.ArrayList<>(rows.subList(0,LedgerPage.SIZE));
            return new LedgerPage(rows,dao.months(),more);
        });
    }
    @Override public List<CartoesEntity> cards() { return dao.cards(); }
    @Override public List<FinanceModels.Purchase> invoicePurchases(long card,YearMonth month) {
        return dao.invoicePurchases(card,month.getMonthValue(),month.getYear());
    }
    @Override public void saveCard(Long id, String title) {
        String validName = name(title);
        db.runInTransaction(() -> {
            for (CartoesEntity card : dao.cards()) {
                if (!card.idCartao.equals(id) && validName.equalsIgnoreCase(card.nomeCartao))
                    throw new IllegalArgumentException("Já existe um cartão com esse nome.");
            }
            CartoesEntity card = id == null ? new CartoesEntity() : dao.card(id);
            if (card == null) throw new IllegalArgumentException("Cartão não encontrado.");
            card.nomeCartao = validName;
            if (id == null) dao.insertCard(card); else dao.updateCard(card);
        });
    }
    @Override public void deleteCard(long id) {
        db.runInTransaction(() -> {
            if (dao.cardPurchases(id) != 0) throw new IllegalArgumentException("Exclua as compras deste cartão antes de removê-lo.");
            dao.deleteCard(id);
        });
    }
    @Override public void savePurchase(Long id, long cardId, String title, long cents, int installments, YearMonth firstMonth) {
        String validName = name(title); amount(cents);
        long[] parts = Money.split(cents, installments);
        db.runInTransaction(() -> {
            if (dao.card(cardId) == null) throw new IllegalArgumentException("Selecione um cartão cadastrado.");
            ComprasCartoesEntity purchase = id == null ? new ComprasCartoesEntity() : dao.purchase(id);
            if (purchase == null) throw new IllegalArgumentException("Compra não encontrada.");
            if (id != null) deletePurchaseValues(id);
            purchase.idCartao = Math.toIntExact(cardId);
            purchase.nomeCompra = validName;
            purchase.valorCompra = cents / 100.0;
            purchase.numeroParcelas = installments;
            purchase.dataPrimeiraParcela = Date.from(firstMonth.atDay(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
            if (id == null) purchase.idCompraCartao = dao.insertPurchase(purchase); else dao.updatePurchase(purchase);
            for (int i = 0; i < parts.length; i++) {
                MesesEntity month = ensureMonth(firstMonth.plusMonths(i));
                ValoresEntity value = new ValoresEntity(validName + " (" + (i + 1) + "/" + installments + ")",
                        parts[i] / 100.0, new Date(), IndicadorValorFixo.NAO_FIXO,
                        IndicadorTipoValor.DESPESA, TipoDespesa.EXTRAS, purchase.idCompraCartao);
                value.idValor = dao.insertValue(value);
                link(month.idMes, value, false);
            }
        });
    }
    private void deletePurchaseValues(long id) {
        for (Long value : dao.purchaseValues(id)) { dao.deleteLinks(value); dao.deleteValue(value); }
    }
    @Override public void deletePurchase(long id) {
        db.runInTransaction(() -> { deletePurchaseValues(id); dao.deletePurchase(id); });
    }
    @Override public File backup(File directory) throws IOException {
        File snapshot = File.createTempFile("myfinances-", ".db", directory);
        try {
            // A consistent standalone SQLite snapshot includes committed WAL data.
            db.getOpenHelper().getWritableDatabase().execSQL("VACUUM INTO ?", new Object[]{snapshot.getAbsolutePath()});
            return snapshot;
        } catch (RuntimeException e) {
            snapshot.delete(); throw e;
        }
    }
}
