package ac.paladin.auto;

import ac.paladin.auto.command.CommandFreeze;
import ac.paladin.auto.command.CommandReload;
import ac.paladin.auto.command.CommandThaw;
import ac.paladin.auto.config.PluginConfig;
import ac.paladin.auto.factory.profile.ProfileFactory;
import ac.paladin.auto.listener.BlockListener;
import ac.paladin.auto.listener.LegacyPickupListener;
import ac.paladin.auto.listener.PickupListener;
import ac.paladin.auto.listener.PlayerListener;
import ac.paladin.auto.manager.IScanManager;
import ac.paladin.auto.manager.ScanManager;
import ac.paladin.auto.model.profile.IProfile;
import ac.paladin.auto.registry.message.IMessageRegistry;
import ac.paladin.auto.registry.message.MessageRegistry;
import ac.paladin.auto.registry.profile.IProfileRegistry;
import ac.paladin.auto.registry.profile.ProfileRegistry;
import ac.paladin.auto.service.IPaladinService;
import ac.paladin.auto.task.AutoThawTask;
import ac.paladin.auto.util.AuthorizationInterceptor;
import co.aikar.commands.BukkitCommandManager;
import co.aikar.commands.MessageType;
import co.aikar.commands.PaperCommandManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.ServicesManager;
import org.bukkit.plugin.java.JavaPlugin;
import retrofit2.Retrofit;
import retrofit2.Retrofit.Builder;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public final class PaladinPlugin extends JavaPlugin {

    private ObjectMapper m_objectMapper;

    private IMessageRegistry m_messageRegistry;

    private IProfileRegistry m_profileRegistry;

    private BukkitCommandManager m_commandManager;

    private OkHttpClient m_httpClient;

    private IPaladinService m_paladinService;

    private IScanManager m_scanManager;

    private PluginConfig m_pluginConfig;

    @Override
    public void onEnable() {
        registerObjectMapper();
        loadPluginConfig();
        if (m_pluginConfig.getApiKey().isEmpty()) {
            throw new RuntimeException("No API key set");
        }

        registerMessages();
        registerProfiles();
        registerHttpClient();
        registerServices();
        registerScanManager();
        registerCommands();
        registerTasks();
        registerListeners();
        exposeServices();
    }

    @Override
    public void onDisable() {
        if (m_commandManager != null) {
            m_commandManager.unregisterCommands();
        }

        if (m_profileRegistry != null) {
            for (IProfile value : m_profileRegistry.getAllProfiles().values()) {
                if (value.isFrozen()) {
                    Player player = getServer().getPlayer(value.getUuid());
                    if (player != null) {
                        value.restore(this, player);
                    }
                }
            }

            m_profileRegistry.dispose();
        }

        if (m_scanManager != null) {
            m_scanManager.dispose();
        }

        if (m_messageRegistry != null) {
            m_messageRegistry.dispose();
        }
    }

    public void loadPluginConfig() {
        saveResource("config.json", false);

        File configFile = new File(getDataFolder(), "config.json");

        try {
            this.m_pluginConfig = m_objectMapper.readValue(configFile, PluginConfig.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void registerObjectMapper() {
        this.m_objectMapper = new ObjectMapper();
    }

    private void registerMessages() {
        this.m_messageRegistry = new MessageRegistry(this, m_pluginConfig);
        m_messageRegistry.load();
    }

    private void registerCommandDependencies() {
        m_commandManager.registerDependency(IMessageRegistry.class, m_messageRegistry);
        m_commandManager.registerDependency(IProfileRegistry.class, m_profileRegistry);
        m_commandManager.registerDependency(IPaladinService.class, m_paladinService);
        m_commandManager.registerDependency(IScanManager.class, m_scanManager);
        m_commandManager.registerDependency(PluginConfig.class, m_pluginConfig);
    }

    private void registerCommands() {
        this.m_commandManager = new PaperCommandManager(this);
        m_commandManager.setFormat(MessageType.SYNTAX, ChatColor.RED, ChatColor.RED, ChatColor.RED);

        registerCommandDependencies();
        m_commandManager.registerCommand(new CommandFreeze());
        m_commandManager.registerCommand(new CommandThaw());
        m_commandManager.registerCommand(new CommandReload());
    }

    private void registerTasks() {
        getServer().getScheduler().runTaskTimerAsynchronously(this, new AutoThawTask(this, m_profileRegistry, m_messageRegistry), 20, 20);
    }

    private void registerProfiles() {
        ProfileFactory playerFactory = new ProfileFactory();
        this.m_profileRegistry = new ProfileRegistry(playerFactory);
    }

    private void registerListeners() {
        PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(new PlayerListener(m_profileRegistry, m_scanManager, this, m_pluginConfig), this);

        try {
            pluginManager.registerEvents(new LegacyPickupListener(m_profileRegistry), this);
        } catch (Exception ignored) {
            pluginManager.registerEvents(new PickupListener(m_profileRegistry), this);
        }

        pluginManager.registerEvents(new BlockListener(m_profileRegistry), this);
    }

    private void registerHttpClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.pingInterval(30, TimeUnit.SECONDS);
        builder.addInterceptor(new AuthorizationInterceptor(m_pluginConfig.getApiKey()));
        this.m_httpClient = builder.build();
    }

    private void registerServices() {
        Retrofit retrofit = new Builder()
                .baseUrl("https://api.paladin.ac/v1/")
                .client(m_httpClient)
                .addConverterFactory(JacksonConverterFactory.create(m_objectMapper))
                .build();

        this.m_paladinService = retrofit.create(IPaladinService.class);
    }

    private void registerScanManager() {
        PluginManager pluginManager = getServer().getPluginManager();
        ScanManager scanManager = new ScanManager(m_objectMapper, m_paladinService, m_httpClient, pluginManager);
        scanManager.connect();
        this.m_scanManager = scanManager;
    }

    private void exposeServices() {
        ServicesManager servicesManager = getServer().getServicesManager();
        servicesManager.register(IProfileRegistry.class, m_profileRegistry, this, ServicePriority.High);
    }
}
