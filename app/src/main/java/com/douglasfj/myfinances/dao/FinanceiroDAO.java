package com.douglasfj.myfinances.dao;

import androidx.room.*;
import com.douglasfj.myfinances.models.entities.*;
import com.douglasfj.myfinances.data.FinanceModels.*;
import java.util.List;

@Dao
public interface FinanceiroDAO {
    @RawQuery List<com.douglasfj.myfinances.data.LedgerRow> ledger(androidx.sqlite.db.SupportSQLiteQuery query);
    @Query("UPDATE MESES_VALORES SET INDICADOR_DESPESA_PAGA=:paid WHERE ID_MES IN (SELECT ID_MES FROM MESES WHERE MES=:month AND ANO=:year) " +
            "AND ID_VALOR IN (SELECT V.ID_VALOR FROM VALORES V JOIN COMPRAS_CARTOES P ON P.ID_COMPRA_CARTAO=V.ID_COMPRA_CARTAO WHERE P.ID_CARTAO=:card)")
    void setInvoicePaid(long card, int month, int year, int paid);
    @Query("SELECT DISTINCT P.*,C.NOME_CARTAO AS cardName FROM COMPRAS_CARTOES P JOIN CARTOES C ON C.ID_CARTAO=P.ID_CARTAO " +
            "JOIN VALORES V ON V.ID_COMPRA_CARTAO=P.ID_COMPRA_CARTAO JOIN MESES_VALORES MV ON MV.ID_VALOR=V.ID_VALOR " +
            "JOIN MESES M ON M.ID_MES=MV.ID_MES WHERE P.ID_CARTAO=:card AND M.MES=:month AND M.ANO=:year ORDER BY P.ID_COMPRA_CARTAO DESC")
    List<Purchase> invoicePurchases(long card,int month,int year);
    @Insert long insertMonth(MesesEntity month);
    @Insert long insertValue(ValoresEntity value);
    @Insert long insertCard(CartoesEntity card);
    @Insert long insertPurchase(ComprasCartoesEntity purchase);
    @Insert long insertLink(MesesValoresEntity link);
    @Update void updateValue(ValoresEntity value);
    @Update void updateCard(CartoesEntity card);
    @Update void updatePurchase(ComprasCartoesEntity purchase);

    @Query("SELECT * FROM MESES WHERE MES=:month AND ANO=:year LIMIT 1")
    MesesEntity month(int month, int year);
    @Query("SELECT * FROM MESES ORDER BY ANO, MES") List<MesesEntity> months();
    @Query("SELECT * FROM VALORES WHERE ID_VALOR=:id") ValoresEntity value(long id);
    @Query("SELECT * FROM CARTOES WHERE ID_CARTAO=:id") CartoesEntity card(long id);
    @Query("SELECT * FROM COMPRAS_CARTOES WHERE ID_COMPRA_CARTAO=:id") ComprasCartoesEntity purchase(long id);
    @Query("SELECT * FROM CARTOES ORDER BY NOME_CARTAO COLLATE NOCASE") List<CartoesEntity> cards();
    @Query("SELECT P.*, C.NOME_CARTAO AS cardName FROM COMPRAS_CARTOES P JOIN CARTOES C ON C.ID_CARTAO=P.ID_CARTAO ORDER BY P.ID_COMPRA_CARTAO DESC")
    List<Purchase> purchases();
    @Query("SELECT V.*, MV.ID_MES_VALOR AS linkId, MV.INDICADOR_DESPESA_PAGA AS paid, C.NOME_CARTAO AS cardName " +
            "FROM VALORES V JOIN MESES_VALORES MV ON MV.ID_VALOR=V.ID_VALOR " +
            "LEFT JOIN COMPRAS_CARTOES P ON P.ID_COMPRA_CARTAO=V.ID_COMPRA_CARTAO " +
            "LEFT JOIN CARTOES C ON C.ID_CARTAO=P.ID_CARTAO WHERE MV.ID_MES=:monthId ORDER BY V.ID_VALOR DESC")
    List<Entry> entries(long monthId);
    @Query("SELECT * FROM VALORES WHERE MES_INICIO_FIXO IS NOT NULL AND MES_INICIO_FIXO<=:key " +
            "AND (MES_FIM_FIXO IS NULL OR MES_FIM_FIXO>=:key) AND ID_COMPRA_CARTAO IS NULL")
    List<ValoresEntity> recurring(int key);
    @Query("SELECT COUNT(*) FROM MESES_VALORES WHERE ID_MES=:month AND ID_VALOR=:value")
    int linkCount(long month, long value);
    @Query("SELECT M.*, COALESCE(SUM(CASE WHEN V.INDICADOR_TIPO_VALOR=1 THEN V.VALOR_REAIS ELSE 0 END),0) AS income, " +
            "COALESCE(SUM(CASE WHEN V.INDICADOR_TIPO_VALOR=0 THEN V.VALOR_REAIS ELSE 0 END),0) AS expenses, " +
            "COALESCE(SUM(CASE WHEN V.IMPOSTO=1 THEN V.VALOR_REAIS ELSE 0 END),0) AS taxes " +
            "FROM MESES M LEFT JOIN MESES_VALORES MV ON MV.ID_MES=M.ID_MES LEFT JOIN VALORES V ON V.ID_VALOR=MV.ID_VALOR " +
            "GROUP BY M.ID_MES ORDER BY M.ANO DESC, M.MES DESC")
    List<Summary> history();
    @Query("UPDATE MESES_VALORES SET INDICADOR_DESPESA_PAGA=:paid WHERE ID_MES_VALOR=:link")
    void setPaid(long link, int paid);
    @Query("DELETE FROM MESES_VALORES WHERE ID_VALOR=:id") void deleteLinks(long id);
    @Query("DELETE FROM VALORES WHERE ID_VALOR=:id") void deleteValue(long id);
    @Query("DELETE FROM MESES_VALORES WHERE ID_VALOR=:id AND ID_MES IN (SELECT ID_MES FROM MESES WHERE ANO*12+MES-1>=:key)")
    void deleteLinksFrom(long id, int key);
    @Query("SELECT ID_VALOR FROM VALORES WHERE ID_COMPRA_CARTAO=:id") List<Long> purchaseValues(long id);
    @Query("DELETE FROM COMPRAS_CARTOES WHERE ID_COMPRA_CARTAO=:id") void deletePurchase(long id);
    @Query("SELECT COUNT(*) FROM COMPRAS_CARTOES WHERE ID_CARTAO=:id") int cardPurchases(long id);
    @Query("DELETE FROM CARTOES WHERE ID_CARTAO=:id") void deleteCard(long id);
}
