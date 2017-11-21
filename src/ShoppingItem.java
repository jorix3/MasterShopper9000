import javax.persistence.*;

/**
 * ShoppingItem
 *
 * @author      Jyri Virtaranta jyri.virtaranta@cs.tamk.fi
 * @version     2017.11.05
 * @since       1.8
 */
@Entity
@Table(name = "SHOPPING_ITEM")
public class ShoppingItem {
    @Id
    @Column(name = "Name", unique = true)
    private String name;
    @Column(name = "Amount")
    private int amount;

    /**
     * Instantiates a new ShoppingItem.
     */
    public ShoppingItem() {

    }

    /**
     * Instantiates a new ShoppingItem.
     *
     * @param   amount  the amount of this item
     * @param   name    the name of this item
     */
    public ShoppingItem(int amount, String name) {
        setAmount(amount);
        setName(name);
    }

    /**
     * Gets amount of this item.
     *
     * @return      the amount of this item
     */
    public int getAmount() {
        return amount;
    }

    /**
     * Sets amount of this item.
     *
     * @param   amount      the amount of this item
     */
    public void setAmount(int amount) {
        this.amount = amount;
    }

    /**
     * Gets name of this item.
     *
     * @return      the name of this item
     */
    public String getName() {
        return name;
    }

    /**
     * Sets name of this item.
     *
     * @param   name    the name of this item
     */
    public void setName(String name) {
        this.name = name;
    }
}
