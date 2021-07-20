package com.mysafe.lib_base.http.model;

import java.util.List;

/**
 * 分页数据
 * @param <Gene>
 */
public class Mod_Page<Gene> {
    /**
     * 页码
     */
    public int page;
    /**
     * 当前获取的一页的数量
     */
    public int rows;
    /**
     * 不分页的数据总数
     */
    public int totals;
    /**
     * 不同的数据体
     */
    public List<Gene> data;

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    public int getTotals() {
        return totals;
    }

    public void setTotals(int totals) {
        this.totals = totals;
    }

    public List<Gene> getData() {
        return data;
    }

    public void setData(List<Gene> data) {
        this.data = data;
    }
}
