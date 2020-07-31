package xyz.sophialaura.hopper;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import xyz.sophialaura.hopper.model.SellItem;

public class HopperSettings {

    private final HopperTier hopperTier;
    private final FileConfiguration fileConfiguration;

    private String alreadyHasHopper;
    private String successfullyEvolved;

    private int glassPanelData;
    private String title;
    private String clickToUpgrade;
    private String currentLevel;
    private String alreadyUpdated;
    private String needUpdateOtherTier;

    private int tierOnePrice;
    private int tierTwoPrice;

    public HopperSettings(HopperTier hopperTier) {
        this.hopperTier = hopperTier;
        this.fileConfiguration = hopperTier.getConfig();
    }

    public void loadConfigurations() {
        fileConfiguration.getConfigurationSection("items").getKeys(false).forEach(s -> {
            String type = fileConfiguration.getString("items." + s + ".type");
            double price = fileConfiguration.getDouble("items." + s + ".sellprice");

            Material material = Material.getMaterial(type);
            if (material == null) {
                System.out.println("[HopperTier] - Can't find material type '" + type + "'.");
                return;
            }

            SellItem sellItem = new SellItem(material, price);
            hopperTier.getSellItemService().add(sellItem);
            System.out.println("[HopperTier] - Added item '" + type + "'. Price: " + price + ".");
        });

        alreadyHasHopper = fileConfiguration.getString("messages.already-has-hopper");
        successfullyEvolved = fileConfiguration.getString("messages.successfully-evolved");

        glassPanelData = fileConfiguration.getInt("menu.glass-panel-data");
        title = fileConfiguration.getString("menu.title");
        clickToUpgrade = fileConfiguration.getString("menu.click-to-upgrade");
        currentLevel = fileConfiguration.getString("menu.current-level");
        alreadyUpdated = fileConfiguration.getString("menu.already-updated");
        needUpdateOtherTier = fileConfiguration.getString("menu.need-update-other-tier");

        tierOnePrice = fileConfiguration.getInt("price.tier-1");
        tierTwoPrice = fileConfiguration.getInt("price.tier-2");
    }

    public HopperTier getHopperTier() {
        return hopperTier;
    }

    public String getAlreadyHasHopper() {
        return alreadyHasHopper;
    }

    public String getSuccessfullyEvolved() {
        return successfullyEvolved;
    }

    public int getGlassPanelData() {
        return glassPanelData;
    }

    public String getTitle() {
        return title;
    }

    public String getClickToUpgrade() {
        return clickToUpgrade;
    }

    public String getCurrentLevel() {
        return currentLevel;
    }

    public String getAlreadyUpdated() {
        return alreadyUpdated;
    }

    public String getNeedUpdateOtherTier() {
        return needUpdateOtherTier;
    }

    public int getTierOnePrice() {
        return tierOnePrice;
    }

    public int getTierTwoPrice() {
        return tierTwoPrice;
    }

    public FileConfiguration getFileConfiguration() {
        return fileConfiguration;
    }
}
