package com.douglasfj.myfinances.data;

import com.douglasfj.myfinances.models.entities.MesesEntity;
import java.util.List;

public final class LedgerPage {
    public static final int SIZE = 30;
    public final List<LedgerRow> rows;
    public final List<MesesEntity> months;
    public final boolean hasMore;
    public LedgerPage(List<LedgerRow> rows, List<MesesEntity> months, boolean hasMore) {
        this.rows = rows; this.months = months; this.hasMore = hasMore;
    }
}
