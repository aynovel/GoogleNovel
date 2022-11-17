package life.forever.cf.entry;

public class PayInfo {

    public String pay_id;
    public String pay_originalJson;
    public String signature;
    public String aMount;
    public String expend;
    public String order_name;

    public String getPay_id() {
        return pay_id;
    }

    public void setPay_id(String pay_id) {
        this.pay_id = pay_id;
    }

    public String getPay_originalJson() {
        return pay_originalJson;
    }

    public void setPay_originalJson(String pay_originalJson) {
        this.pay_originalJson = pay_originalJson;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String mSignature) {
        signature = mSignature;
    }

    public String getaMount() {
        return aMount;
    }

    public void setaMount(String aMount) {
        this.aMount = aMount;
    }

    public String getExpend() {
        return expend;
    }

    public void setExpend(String expend) {
        this.expend = expend;
    }

    public String getOrder_name() {
        return order_name;
    }

    public void setOrder_name(String order_name) {
        this.order_name = order_name;
    }
}
