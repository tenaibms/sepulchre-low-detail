package me.tenai;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.config.Range;

@ConfigGroup("Sepulchre Low Detail")
public interface SepulchreLowDetailConfig extends Config
{
	@ConfigSection(name = "All Floors", description = "All Floor Settings", position = -1000, closedByDefault = true)
	String all_floor_section = "All Floors";
	
	@ConfigSection(name = "Floor 1-2", description = "Floor 1-2 settings", position = -999, closedByDefault = true)
	String floor_12_section = "Floor 1-2";
	
	@ConfigSection(name = "Floor 3-4", description = "Floor 3-4 settings", position = -998, closedByDefault = true)
	String floor_34_section = "Floor 3-4";
	
	@ConfigSection(name = "Floor 5", description = "Floor 5 settings", position = -997, closedByDefault = true)
	String floor_5_section = "Floor 5";
	
	/* all floors */
	@ConfigItem(keyName = "brightnessDifference", name = "Brightness Difference", description  = "Difference between shadows and normal colors, if the brightness goes below zero,  it will be wrapped to zero.", position = 1, section=all_floor_section)
	default int brightnessDifference() {
		return 20;
	}
	
	@Range(min = 0, max = 8)
	@ConfigItem(keyName = "blend", name = "Blend Radius", description  = "Radius of the blending of shadows,", position = 2, section=all_floor_section)
	default int blendRadius() {
		return 2;
	}
	
	/* floor 1-2 */
	@Range(min = 0, max = 63)
	@ConfigItem(keyName = "floor12hue", name = "Floor 1-2 Hue", description = "", position = 3, section=floor_12_section)
	default int floor12hue() {
		return 21;
	}

	@Range(min = 0, max = 7)
	@ConfigItem(keyName = "floor12saturation", name = "Floor 1-2 Saturation", description = "", position = 4, section=floor_12_section)
	default int floor12saturation() {
		return 0;
	}

	@Range(min = 0, max = 127)
	@ConfigItem(keyName = "floor12brightness", name = "Floor 1-2 Brightness", description  = "", position = 5, section=floor_12_section)
	default int floor12brightness() {
		return 33;
	}
	
	/* floor 3-4 */
	@Range(min = 0, max = 63)
	@ConfigItem(keyName = "floor34hue", name = "Floor 3-4 Hue", description = "", position = 6, section=floor_34_section)
	default int floor34hue() {
		return 32;
	}
	
	@Range(min = 0, max = 7)
	@ConfigItem(keyName = "floor34saturation", name = "Floor 3-4 Saturation", description = "", position = 7, section=floor_34_section)
	default int floor34saturation() {
		return 0;
	}
	
	@Range(min = 0, max = 127)
	@ConfigItem(keyName = "floor34brightness", name = "Floor 3-4 Brightness", description  = "", position = 8, section=floor_34_section)
	default int floor34brightness() {
		return 33;
	}

	/* floor 5 */
	@Range(min = 0, max = 63)
	@ConfigItem(keyName = "floor5hue", name = "Floor 5 Hue", description = "", position = 9, section=floor_5_section)
	default int floor5hue() {
		return 32;
	}
	
	@Range(min = 0, max = 7)
	@ConfigItem(keyName = "floor5saturation", name = "Floor 5 Saturation", description = "", position = 10, section=floor_5_section)
	default int floor5saturation() {
		return 1;
	}
	
	@Range(min = 0, max = 127)
	@ConfigItem(keyName = "floor5brightness", name = "Floor 5 Brightness", description  = "", position = 11, section=floor_5_section)
	default int floor5brightness() {
		return 30;
	}
}