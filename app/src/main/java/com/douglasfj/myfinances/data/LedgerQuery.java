package com.douglasfj.myfinances.data;

import androidx.sqlite.db.SimpleSQLiteQuery;
import androidx.sqlite.db.SupportSQLiteQuery;
import java.time.YearMonth;
import java.util.*;

public final class LedgerQuery {
    private LedgerQuery() {}
    public static SupportSQLiteQuery page(boolean income, String search, YearMonth month, int offset) {
        String normal = "SELECT MV.ID_MES_VALOR AS rowId,V.ID_VALOR AS valueId,NULL AS cardId," +
                "M.MES AS month,M.ANO AS year,V.NOME_VALOR AS name,V.VALOR_REAIS/100.0 AS amount," +
                "COALESCE(MV.INDICADOR_DESPESA_PAGA,0) AS paid,COALESCE(V.INDICADOR_VALOR_FIXO,0) AS fixed,V.IMPOSTO AS tax " +
                "FROM VALORES V JOIN MESES_VALORES MV ON MV.ID_VALOR=V.ID_VALOR JOIN MESES M ON M.ID_MES=MV.ID_MES " +
                "WHERE V.ID_COMPRA_CARTAO IS NULL AND V.INDICADOR_TIPO_VALOR=" + (income ? "1" : "0");
        String invoices = "SELECT C.ID_CARTAO AS rowId,NULL AS valueId,C.ID_CARTAO AS cardId,M.MES AS month,M.ANO AS year," +
                "'Fatura ' || C.NOME_CARTAO AS name,SUM(V.VALOR_REAIS)/100.0 AS amount," +
                "MIN(CASE WHEN MV.INDICADOR_DESPESA_PAGA=1 THEN 1 ELSE 0 END) AS paid,0 AS fixed,0 AS tax " +
                "FROM VALORES V JOIN MESES_VALORES MV ON MV.ID_VALOR=V.ID_VALOR JOIN MESES M ON M.ID_MES=MV.ID_MES " +
                "JOIN COMPRAS_CARTOES P ON P.ID_COMPRA_CARTAO=V.ID_COMPRA_CARTAO JOIN CARTOES C ON C.ID_CARTAO=P.ID_CARTAO " +
                "GROUP BY M.ID_MES,C.ID_CARTAO";
        String sql = "SELECT * FROM (" + normal + (income ? "" : " UNION ALL " + invoices) + ") WHERE " +
                "INSTR(" + lowerName("COALESCE(name,'')") + ",?)>0";
        List<Object> args = new ArrayList<>(); args.add(search == null ? "" : search.trim().toLowerCase(Locale.ROOT));
        if (month != null) { sql += " AND year=? AND month=?"; args.add(month.getYear()); args.add(month.getMonthValue()); }
        sql += " ORDER BY year DESC,month DESC,rowId DESC,COALESCE(cardId,0) DESC LIMIT ? OFFSET ?";
        args.add(LedgerPage.SIZE + 1); args.add(Math.max(0,offset));
        return new SimpleSQLiteQuery(sql,args.toArray());
    }
    static String lowerName(String sql) {
        String upper="ÁÀÂÃÉÊÍÓÔÕÚÇ",lower="áàâãéêíóôõúç";
        for(int i=0;i<upper.length();i++) sql="REPLACE("+sql+",'"+upper.charAt(i)+"','"+lower.charAt(i)+"')";
        return "LOWER("+sql+")";
    }
}
