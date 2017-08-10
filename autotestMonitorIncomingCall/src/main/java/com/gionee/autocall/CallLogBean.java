package com.gionee.autocall;

/**
 * Created by stao_nebula on 2017/7/10.
 */

class CallLogBean {
    public int col;
    public int row;
    public String content;

    public CallLogBean() {
    }

    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public CallLogBean(int col, int row, String content) {
        this.col = col;
        this.row = row;
        this.content = content;
    }

    @Override
    public String toString() {
        return "CallLogBean{" +
                "col=" + col +
                ", row=" + row +
                ", content='" + content + '\'' +
                '}';
    }
}
