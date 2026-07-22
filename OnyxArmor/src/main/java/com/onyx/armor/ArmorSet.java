package com.onyx.armor;
 
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.potion.PotionEffectType;
 
import java.util.List;
import java.util.Map;
 
public class ArmorSet {
 
    private final String id;
    private final String displayName;
    private final Material material;
    private final Color leatherColor;      // null if not leather
    private final List<String> lore;
    private final Map<PotionEffectType, Integer> effects;
    private final boolean glow;
    private final boolean partialBonus;
 
    public ArmorSet(
        String id,
        String displayName,
        Material material,
        Color leatherColor,
        List<String> lore,
        Map<PotionEffectType, Integer> effects,
        boolean glow,
        boolean partialBonus
    ) {
        this.id = id;
        this.displayName = displayName;
        this.material = material;
        this.leatherColor = leatherColor;
        this.lore = lore;
        this.effects = effects;
        this.glow = glow;
        this.partialBonus = partialBonus;
    }
 
    public String getId()                          { return id; }
    public String getDisplayName()                 { return displayName; }
    public Material getMaterial()                  { return material; }
    public Color getLeatherColor()                 { return leatherColor; }
    public List<String> getLore()                  { return lore; }
    public Map<PotionEffectType, Integer> getEffects() { return effects; }
    public boolean isGlow()                        { return glow; }
    public boolean isPartialBonus()                { return partialBonus; }
    public boolean isLeather()                     { return leatherColor != null; }
}
