package com.douglasfj.myfinances.data;

import androidx.sqlite.db.SimpleSQLiteQuery;
import androidx.sqlite.db.SupportSQLiteQuery;
import java.time.YearMonth;
import java.util.*;

/** One row per purchase; the month refers to an actual installment in that invoice. */
public final class PurchaseQuery {
    private PurchaseQuery() {}
    public static SupportSQLiteQuery page(String search, YearMonth month, Long card, int offset) {
        String sql = "SELECT P.*, C.NOME_CARTAO AS cardName FROM COMPRAS_CARTOES P " +
                "JOIN CARTOES C ON C.ID_CARTAO=P.ID_CARTAO WHERE INSTR(" + LedgerQuery.lowerName("COALESCE(P.NOME_COMPRA,'')") + ",?)>0";
        List<Object> args = new ArrayList<>(); args.add(search == null ? "" : search.trim().toLowerCase(Locale.ROOT));
        if (card != null) { sql += " AND P.ID_CARTAO=?"; args.add(card); }
        if (month != null) {
            sql += " AND EXISTS (SELECT 1 FROM VALORES V JOIN MESES_VALORES MV ON MV.ID_VALOR=V.ID_VALOR " +
                    "JOIN MESES M ON M.ID_MES=MV.ID_MES WHERE V.ID_COMPRA_CARTAO=P.ID_COMPRA_CARTAO AND M.MES=? AND M.ANO=?)";
            args.add(month.getMonthValue()); args.add(month.getYear());
        }
        sql += " ORDER BY P.ID_COMPRA_CARTAO DESC LIMIT ? OFFSET ?";
        args.add(LedgerPage.SIZE + 1); args.add(Math.max(0,offset));
        return new SimpleSQLiteQuery(sql,args.toArray());
    }
}
