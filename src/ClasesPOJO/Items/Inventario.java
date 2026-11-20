package ClasesPOJO.Items;

import java.util.ArrayList;
import java.util.List;

public class Inventario {
    private final List<Item> items;

    public Inventario() {
        this.items = new ArrayList<>();
    }

    public void agregarItem(Item item) {
        if (item != null) {
            items.add(item);
        }
    }

    public boolean removerItem(Item item) {
        return items.remove(item);
    }

    public Item buscarPorNombre(String nombre) {
        for (Item item : items) {
            if (item.getNombre().equalsIgnoreCase(nombre.trim())) {
                return item;
            }
        }
        return null;
    }

    public boolean tieneItem(String nombre) {
        return buscarPorNombre(nombre) != null;
    }

    public List<Item> getItems() {
        return items;
    }
}
