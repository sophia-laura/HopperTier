package xyz.sophialaura.hopper;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.sophialaura.hopper.commands.HopperCommand;
import xyz.sophialaura.hopper.database.DatabaseConnection;
import xyz.sophialaura.hopper.database.dao.HopperDao;
import xyz.sophialaura.hopper.database.dao.HopperDaoImpl;
import xyz.sophialaura.hopper.database.sqlite.SqliteConnection;
import xyz.sophialaura.hopper.listener.HopperListener;
import xyz.sophialaura.hopper.menu.api.MenuListener;
import xyz.sophialaura.hopper.model.Hopper;
import xyz.sophialaura.hopper.services.hopper.HopperService;
import xyz.sophialaura.hopper.services.hopper.HopperServiceImpl;
import xyz.sophialaura.hopper.services.item.SellItemService;
import xyz.sophialaura.hopper.services.item.SellItemServiceImpl;

import java.io.File;
import java.io.IOException;

public class HopperTier extends JavaPlugin {

    private static HopperTier instance;

    private DatabaseConnection databaseConnection;
    private SellItemService sellItemService;
    private HopperService hopperService;
    private HopperDao hopperDao;

    private HopperSettings hopperSettings;
    private Economy economy;

    @Override
    public void onLoad() {
        instance = this;

        final File file = new File(getDataFolder(), "database.db");
        saveDefaultConfig();
        createFileOrIgnore(file);

        databaseConnection = new SqliteConnection(file);
        databaseConnection.setupConnection();

        sellItemService = new SellItemServiceImpl();
        hopperService = new HopperServiceImpl();

        hopperDao = new HopperDaoImpl(databaseConnection);

        hopperDao.createTables();

        hopperSettings = new HopperSettings(this);
        hopperSettings.loadConfigurations();
    }

    @Override
    public void onEnable() {
        setupVault();

        getServer().getPluginManager().registerEvents(new MenuListener(), this);
        getServer().getPluginManager().registerEvents(new HopperListener(this), this);

        for (Hopper hopper : hopperDao.findAll()) {
            hopperService.add(hopper);
        }

        getCommand("ehopper").setExecutor(new HopperCommand(this));
    }

    @Override
    public void onDisable() {

    }

    private void createFileOrIgnore(File file) {
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void setupVault() {
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return;
        }
        this.economy = rsp.getProvider();
    }

    public SellItemService getSellItemService() {
        return sellItemService;
    }

    public HopperService getHopperService() {
        return hopperService;
    }

    public HopperDao getHopperDao() {
        return hopperDao;
    }

    public DatabaseConnection getDatabaseConnection() {
        return databaseConnection;
    }

    public Economy getEconomy() {
        return economy;
    }

    public HopperSettings getHopperSettings() {
        return hopperSettings;
    }

    public static HopperTier getInstance() {
        return instance;
    }
}
