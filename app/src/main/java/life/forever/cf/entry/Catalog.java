package life.forever.cf.entry;


public class Catalog {

    public int id;
    public String title;
    public int sort;
    public int isvip;
    public int vip;
    public int createtime;
    public int updatetime;
    public int order;
    @Override
    public boolean equals(Object obj) {
        return obj != null
                && obj instanceof Catalog
                && this.id == ((Catalog) obj).id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public int getIsvip() {
        return isvip;
    }

    public void setIsvip(int isvip) {
        this.isvip = isvip;
    }

    public int getVip() {
        return vip;
    }

    public void setVip(int vip) {
        this.vip = vip;
    }

    public int getCreatetime() {
        return createtime;
    }

    public void setCreatetime(int createtime) {
        this.createtime = createtime;
    }

    public int getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(int updatetime) {
        this.updatetime = updatetime;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }
}
