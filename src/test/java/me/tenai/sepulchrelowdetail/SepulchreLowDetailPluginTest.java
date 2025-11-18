package me.tenai.sepulchrelowdetail;

import me.tenai.sepulchrelowdetail.SepulchreLowDetailPlugin;
import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class SepulchreLowDetailPluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(SepulchreLowDetailPlugin.class);
		RuneLite.main(args);
	}
}