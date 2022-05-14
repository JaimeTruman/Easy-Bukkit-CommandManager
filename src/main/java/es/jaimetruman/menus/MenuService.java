package es.jaimetruman.menus;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class MenuService {
    private final MenuInventoryBuilder inventoryBuilder;
    private final OpenMenuRepository openMenuRepository;

    public MenuService() {
        this.openMenuRepository = InstanceProvider.OPEN_MENUS_REPOSITORY;
        this.inventoryBuilder = new MenuInventoryBuilder();
    }

    public void open(Player player, Menu menu){
        Inventory inventory = this.inventoryBuilder.build(menu.configuration(), menu.items());
        player.openInventory(inventory);

        this.openMenuRepository.save(player.getName(), menu);
    }
}