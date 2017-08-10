package com.gionee.autocall.bean;

/**
 * Created by stao_nebula on 2017/7/5.
 */

public class CallLogBean {

    public int row;
    public int col;
    public String content;

    public CallLogBean() {
    }

    /**
     * Instantiates a new Call log bean.
     *
     * @param col     the col 行
     * @param row     the row 列
     * @param content the content 写入数据
     */
    public CallLogBean(int col, int row, String content) {
        this.row = row;
        this.col = col;
        this.content = content;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "CallLogBean{" +
                "row=" + row +
                ", col=" + col +
                ", content='" + content + '\'' +
                '}';
    }
}
