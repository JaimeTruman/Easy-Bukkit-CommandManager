package es.bukkitclassmapper;

import es.bukkitclassmapper._shared.utils.reflections.InstanceProvider;
import es.bukkitclassmapper.commands.CommandMapper;
import es.bukkitclassmapper.events.EventListenerMapper;
import es.bukkitclassmapper.mobs.MobMapper;
import es.bukkitclassmapper.task.TaskMapper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.plugin.Plugin;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static es.bukkitclassmapper._shared.utils.ExceptionUtils.*;

@AllArgsConstructor
public final class ClassMapperConfiguration {
    @Getter private final Plugin plugin;
    @Getter private final String commonPackage;
    @Getter private final InstanceProvider instanceProvider;
    @Getter private final Set<Class<? extends ClassMapper>> mappers;
    @Getter private final Executor commonThreadPool;
    @Getter private final Executor IOThreadPool;
    @Getter private final boolean waitUntilCompletion;
    @Getter private final String onWrongPermissions;
    @Getter private final String onCommandNotFound;

    @SneakyThrows
    public void startScanning() {
        CountDownLatch mappersCompleted = new CountDownLatch(this.mappers.size());

        this.mappers.stream()
                .map(mapperClass -> runAndGetOrTerminate(() -> mapperClass.getConstructors()[0].newInstance(this)))
                .map(mapperInstance -> (ClassMapper) mapperInstance)
                .forEach(mapperInstance -> {
                    commonThreadPool.execute(mapperInstance::scan);
                    mappersCompleted.countDown();
                });

        if(this.waitUntilCompletion) mappersCompleted.await();
    }

    private static class ClassMapperConfigurationBuilder {
        @Getter private final Plugin plugin;
        @Getter private final String commonPackage;
        @Getter private InstanceProvider instanceProvider;
        @Getter private Set<Class<? extends ClassMapper>> mappers;
        @Getter private boolean waitUntilCompletion;
        @Getter private Executor commonThreadPool;
        @Getter private Executor IOThreadPool;
        @Getter private String onWrongPermissions;
        @Getter private String onCommandNotFound;

        public ClassMapperConfigurationBuilder(Plugin plugin, String commonPackage) {
            this.instanceProvider = InstanceProvider.defaultProvider();
            this.commonPackage = commonPackage;
            this.mappers = new HashSet<>();
            this.plugin = plugin;
            this.commonThreadPool = Executors.newSingleThreadExecutor();
            this.IOThreadPool = Executors.newSingleThreadExecutor();
        }

        public ClassMapperConfigurationBuilder commonThreadPool(Executor executor) {
            this.commonThreadPool = executor;
            return this;
        }

        public ClassMapperConfigurationBuilder IOThreadPool(Executor executor) {
            this.IOThreadPool = executor;
            return this;
        }

        public ClassMapperConfigurationBuilder waitUntilCompletion() {
            this.waitUntilCompletion = true;
            return this;
        }

        public ClassMapperConfigurationBuilder commandMapper (String onWrongPermissions, String onCommandNotFound) {
            this.onWrongPermissions = onWrongPermissions;
            this.onCommandNotFound = onCommandNotFound;

            this.mappers.add(CommandMapper.class);

            return this;
        }

        public ClassMapperConfigurationBuilder mobMapper () {
            this.mappers.add(MobMapper.class);
            return this;
        }

        public ClassMapperConfigurationBuilder eventListenerMapper () {
            this.mappers.add(EventListenerMapper.class);
            return this;
        }

        public ClassMapperConfigurationBuilder taskMapper () {
            this.mappers.add(TaskMapper.class);
            return this;
        }

        public ClassMapperConfigurationBuilder instanceProvider(InstanceProvider instanceProvider) {
            this.instanceProvider = instanceProvider;
            return this;
        }
    }
}