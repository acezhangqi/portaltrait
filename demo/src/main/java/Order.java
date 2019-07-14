/**
 * @author zhangqi
 * @create 2019/7/14
 */
public class Order {
    private String userId;
    private Double amount;

    public Order() {
    }

    public Order(String userId, Double amount) {
        this.userId = userId;
        this.amount = amount;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }
}
