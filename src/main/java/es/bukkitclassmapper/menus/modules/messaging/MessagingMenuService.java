package es.bukkitclassmapper.menus.modules.messaging;

import es.bukkitclassmapper._shared.utils.reflections.ClassMapperInstanceProvider;
import es.bukkitclassmapper.commands.exceptions.InvalidUsage;
import es.bukkitclassmapper.menus.Menu;
import es.bukkitclassmapper.menus.repository.OpenMenuRepository;
import java.util.function.Consumer;

public final class MessagingMenuService {
    private final OpenMenuRepository openMenuRepository;

    public MessagingMenuService() {
        this.openMenuRepository = ClassMapperInstanceProvider.OPEN_MENUS_REPOSITORY;
    }

    public <T> void broadCastMessage(Menu originalMenu, T message){
        this.openMenuRepository.findByMenuType(originalMenu.getClass()).stream()
                .filter(menu -> !menu.getMenuId().equals(originalMenu.getMenuId()))
                .parallel()
                .forEach(menuOfPlayer -> sendMessageToMenu(message, menuOfPlayer));
    }

    public <T> void broadCastMessage(Class<? extends Menu> menuTypeTarget, T message){
        this.openMenuRepository.findByMenuType(menuTypeTarget).stream()
                .parallel()
                .forEach(menuOfPlayer -> sendMessageToMenu(message, menuOfPlayer));
    }

    private <T> void sendMessageToMenu(T message, Menu menuOfPlayer) {
        if(!menuOfPlayer.getConfiguration().hasMessagingConfiguration())
            throw new InvalidUsage("No messaging configuration added for menus");

        Consumer<T> onMessageListener = (Consumer<T>) menuOfPlayer.getConfiguration().getMessageListener(message.getClass());
        if(onMessageListener == null)
            throw new InvalidUsage("No message listener added for menu");

        onMessageListener.accept(message);
    }
}