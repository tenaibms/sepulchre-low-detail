package me.tenai;

import com.google.inject.Provides;
import javax.inject.Inject;
import javax.swing.SwingUtilities;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.GameState;
import net.runelite.api.SceneTileModel;
import net.runelite.api.SceneTilePaint;
import net.runelite.api.Tile;
import net.runelite.api.WorldView;
import net.runelite.api.events.PreMapLoad;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.input.MouseManager;
import net.runelite.client.input.MouseListener;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Slf4j
@PluginDescriptor(
	name = "Sepulchre Low Detail"
)
public class SepulchreLowDetailPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private SepulchreLowDetailConfig config;
	
	@Inject
	private MouseManager mouseManager;
	
	@Inject
	private ClientThread clientThread;
	
	private final HashSet<Integer> ground_object_blacklist = new HashSet<Integer>(Set.of(
		38750,
		38751,
		38752,
		38753,
		38754,
		38755,
		38756,
		38757,
		38758,
		38759,
		38760,
		38761,
		38762,
		38372,
		38373,
		38374,
		38375,
		38376,
		38377,
		38378,
		38379,
		38380,
		38381,
		38382		
	));
	
	private final HashSet<Short> overlay_color = new HashSet<Short>(Set.of(
		(short)359,
		(short)361,
		(short)37,
		(short)362,
		(short)178,
		(short)57,		
		(short)143
	));
				
	private final HashMap<Integer, Integer> floor_regions = new HashMap<Integer, Integer>(Map.of(
		9565, 0, /* lobby */
		9053, 1,
		10077, 2,
		9563, 3,
		10075, 4,
		9051, 5
	));

	private final HashSet<Integer> game_object_blacklist = new HashSet<Integer>(Set.of(
		38427 /* don't shade the flames from the flamethrowers, only the flamethrowers themselves */
	));	

	int[] regions;
	
	int xSize = 0, ySize = 0, zSize = 0;
	
	Tile[][][] tiles;
	short[][][] overlays;
	
	@Override
	protected void startUp() throws Exception
	{
//		mouseManager.registerMouseListener(mouseListener);
		log.info("Old Low Detail started!");
	}

	@Override
	protected void shutDown() throws Exception
	{
		log.info("Old Low Detail stopped!");
	}
	
	private int ShadeColor(int color) {
		int hue = Colors.unpackJagexHue(color);
		int saturation = Colors.unpackJagexSaturation(color);
		int lightness = Colors.unpackJagexLightness(color);
		
		lightness -= config.brightnessDifference();
		lightness = Math.clamp(lightness, Colors.MIN_LIGHTNESS, Colors.MAX_LIGHTNESS);
		
		return Colors.packJagexHsl(hue, saturation, lightness);
	}
	
	private int findFloor(int[] regions) {
		var current_floor = -1;
		for(var region : regions) {
			if(floor_regions.containsKey(region)) { current_floor = floor_regions.get(region); log.info("Current Floor: {}", current_floor); }
		}
		return current_floor;
	}
	
	private int[][][] findObjectCounts() {
		var objectCounts = new int[zSize][ySize][xSize];

		for (var i = 0; i < zSize; i++) {
		    for (var j = 0; j < xSize; j++) {
		        for (var k = 0; k < ySize; k++) {
		            if (tiles[i][j][k] != null && tiles[i][j][k].getGameObjects() != null) {
		            	var count = 0;
		                for(GameObject object : tiles[i][j][k].getGameObjects()) {
		                	if(object != null) {
		                		if(game_object_blacklist.contains(object.getId())) continue;
		                		count++;
		                	}
		                }
		                objectCounts[i][j][k] = count;
		            } else {
		                objectCounts[i][j][k] = 0;
		            }
		        }
		    }
		}
		
		return objectCounts;
	}
	
	private boolean isTileValid(int plane, int x, int y) {
    	if (overlays[plane][x][y] == 42) return false; /* invisible overlay, always invalid */
        if (tiles[plane][x][y] == null || tiles[plane][x][y].getSceneTilePaint() == null) return false; /* if tile is null or has no scene paint, then invalid */
        if (tiles[plane][x][y].getGroundObject() != null) {  /* if tile contains ground object we want to ignore, then invalid */
        	if(ground_object_blacklist.contains(tiles[plane][x][y].getGroundObject().getId())) {
        		return false;
        	}
        };
        
        return overlay_color.contains(overlays[plane][x][y]); /* lastly, check if the tile has a valid overlay, otherwise invalid */
	}
	
	private void paintTile(Tile tile, int color) {
    	SceneTilePaint paint = tile.getSceneTilePaint();       
		paint.setNeColor(color);
		paint.setNwColor(color);
		paint.setSeColor(color);
		paint.setSwColor(color);
	}
	
	@Subscribe
	public void onPreMapLoad(PreMapLoad event) {		
		regions = event.getScene().getMapRegions();

		tiles = event.getScene().getExtendedTiles();
		overlays = event.getScene().getOverlayIds();
		
		zSize = tiles.length;
		xSize = tiles[0].length;
		ySize = tiles[0][0].length;
				
		var floor_colors = new HashMap<Integer, Integer>(Map.of(
			0, Colors.packJagexHsl(config.floor12hue(), config.floor12saturation(), config.floor12brightness()),
			1, Colors.packJagexHsl(config.floor12hue(), config.floor12saturation(), config.floor12brightness()),
			2, Colors.packJagexHsl(config.floor12hue(), config.floor12saturation(), config.floor12brightness()),
			3, Colors.packJagexHsl(config.floor34hue(), config.floor34saturation(), config.floor34brightness()),
			4, Colors.packJagexHsl(config.floor34hue(), config.floor34saturation(), config.floor34brightness()),
			5, Colors.packJagexHsl(config.floor5hue(), config.floor5saturation(), config.floor5brightness())
		));
		
		var current_floor = findFloor(regions);
		if (current_floor == -1) return; /* don't run when no sepulchre regions are loaded */
		
		var object_counts = findObjectCounts();
		
		int[][][] lightness = new int[zSize][xSize][ySize];
		int[][][] blended_lightness = new int[zSize][xSize][ySize];

		
		int color = floor_colors.get(current_floor);
		int color_shaded = ShadeColor(color);
		
		int brightness = Colors.unpackJagexLightness(color);
		int brightness_shaded = Colors.unpackJagexLightness(color_shaded);
		
		/* set lightness array to shaded or unshaded */		
		for (int i = 0; i < tiles.length; ++i) {
			for (int j = 0; j < tiles[i].length; ++j) {
				for (int k = 0; k < tiles[i][j].length; ++k) {
                	if (isTileValid(i, j, k)) {                		
                    	if (object_counts[i][j][k] > 0) {
                    		lightness[i][j][k] = brightness_shaded;

                    	} else {
                    		lightness[i][j][k] = brightness;
                    	}                  		                    	        
                	}
				}
			}
		}
				
		int blend_radius = config.blendRadius();
		
		/* blend tiles in lightness array */
		for(int i = 0; i < tiles.length; ++i) {
			for(int j = 0; j < tiles[i].length; ++j) {
				for (int k = 0; k < tiles[i][j].length; ++k) {           	
                	if(isTileValid(i, j, k)) {
                		double box_sum = 0;
                		int count = 0;
                		
                		for(int bx = -blend_radius; bx <= blend_radius; ++bx) {
                    		for(int by = -blend_radius; by <= blend_radius; ++by) {
                    			if (j + bx < 0 || j + bx >= tiles[i].length || k + by < 0 || k + by > tiles[i][j].length) continue;
                    			if(isTileValid(i, j + bx, k + by)) {
                    				++count;
                        			
                        			int bright = lightness[i][j + bx][k + by];
                        			box_sum += bright;
                    			}                    			
                    		}
                		}
                		
                		blended_lightness[i][j][k]  = (int) (box_sum / count);
                	}
				}
			}
		}
				
		/* paint tiles */
		for(int i = 0; i < tiles.length; ++i) {
			for(int j = 0; j < tiles[i].length; ++j) {
				for (int k = 0; k < tiles[i][j].length; ++k) {
            		if(k < ySize - 1  && j < xSize - 1) {
            			var count = 0;
            			var avg = 0;
            			
            			int[][] rc = { /* relative coordinates */
            				{ j, 	 k 	   },
            				{ j, 	 k + 1 },
            				{ j + 1, k + 1 },
            				{ j + 1, k     }
            			};
            			
            			boolean ne_valid = isTileValid(i, rc[0][0], rc[0][1]);
            			boolean se_valid = isTileValid(i, rc[1][0], rc[1][1]);
            			boolean sw_valid = isTileValid(i, rc[2][0], rc[2][1]);
            			boolean nw_valid = isTileValid(i, rc[3][0], rc[3][1]);
            			
            			if(ne_valid) {
                			avg += blended_lightness[i][rc[0][0]][rc[0][1]];
                			count++;
            			}
            			if(se_valid) {
            				avg += blended_lightness[i][rc[1][0]][rc[1][1]];
            				count++;
            			};
            			if(sw_valid) {
            				avg += blended_lightness[i][rc[2][0]][rc[2][1]];
            				count++;
            			};
            			if(nw_valid) {
            				avg += blended_lightness[i][rc[3][0]][rc[3][1]];
            				count++;
            			};

            			if(count == 0) continue;
            			
            			avg /= count;
            			
                		int hue = Colors.unpackJagexHue(floor_colors.get(current_floor));
                		int saturation = Colors.unpackJagexSaturation(floor_colors.get(current_floor));
            			
            			int blended_color = Colors.packJagexHsl(hue, saturation, avg);
            			
            			var ne_tile = tiles[i][rc[0][0]][rc[0][1]];
            			var se_tile = tiles[i][rc[1][0]][rc[1][1]];
            			var sw_tile = tiles[i][rc[2][0]][rc[2][1]];
            			var nw_tile = tiles[i][rc[3][0]][rc[3][1]];
            			
            			if(ne_valid) {
            				SceneTilePaint paint = ne_tile.getSceneTilePaint();
            				paint.setNeColor(blended_color);
            			}
            			if(se_valid) {
            				SceneTilePaint paint = se_tile.getSceneTilePaint();
            				paint.setSeColor(blended_color);
            			};
            			if(sw_valid) {
            				SceneTilePaint paint = sw_tile.getSceneTilePaint();
            				paint.setSwColor(blended_color);
            			};
            			if(nw_valid) {
            				SceneTilePaint paint = nw_tile.getSceneTilePaint();
            				paint.setNwColor(blended_color);
            			};
                	}
				}
			}
		}
	}
	
	@Subscribe
	public void onConfigChanged(ConfigChanged event) {
		/* reload game upon config change */
		if(event.getGroup().equals("Sepulchre Low Detail")) {
			clientThread.invokeLater(() -> {
				if (client.getGameState() == GameState.LOGGED_IN) {
					client.setGameState(GameState.LOADING);
				}
			});
		}
	}
	
	@Provides
	SepulchreLowDetailConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(SepulchreLowDetailConfig.class);
	}
}
