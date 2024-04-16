package com.apothicon.floating_islands.worldgen;

import com.apothicon.floating_islands.FloatingIslandsMath;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import finalforeach.cosmicreach.blocks.BlockState;
import finalforeach.cosmicreach.savelib.blockdata.IBlockData;
import finalforeach.cosmicreach.savelib.blockdata.SingleBlockData;
import finalforeach.cosmicreach.savelib.blocks.IBlockDataFactory;
import finalforeach.cosmicreach.world.Chunk;
import finalforeach.cosmicreach.world.Zone;
import finalforeach.cosmicreach.worldgen.ChunkColumn;
import finalforeach.cosmicreach.worldgen.ZoneGenerator;
import finalforeach.cosmicreach.worldgen.noise.SimplexNoise;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public class FloatingIslandsZoneGenerator extends ZoneGenerator {
    public static ConcurrentHashMap<Vector2, Integer> heightmap = new ConcurrentHashMap<>();
    BlockState airBlock = this.getBlockStateInstance("base:air[default]");
    BlockState waterBlock = this.getBlockStateInstance("base:water[default]");
    BlockState stoneBasaltBlock = this.getBlockStateInstance("base:stone_basalt[default]");
    BlockState grassBlock = this.getBlockStateInstance("base:grass[default]");
    BlockState grassSlabBlock = this.getBlockStateInstance("base:grass[type=full,slab_type=bottom]");
    BlockState sandBlock = this.getBlockStateInstance("base:sand[default]");
    BlockState sandSlabBlock = this.getBlockStateInstance("base:sand[default,slab_type=bottom]");
    BlockState dirtBlock = this.getBlockStateInstance("base:dirt[default]");
    BlockState dirtSlabBlock = this.getBlockStateInstance("base:dirt[default,slab_type=bottom]");
    BlockState snowBlock = this.getBlockStateInstance("base:snow[default]");
    BlockState cactusBlockZ = this.getBlockStateInstance("floating_islands:cactus[default,slab_type=verticalPosZ]");
    BlockState cactusBlockNegZ = this.getBlockStateInstance("floating_islands:cactus[default,slab_type=verticalNegZ]");
    BlockState cactusBlockX = this.getBlockStateInstance("floating_islands:cactus[default,slab_type=verticalPosX]");
    BlockState cactusBlockNegX = this.getBlockStateInstance("floating_islands:cactus[default,slab_type=verticalNegZ]");
    BlockState logBlock = this.getBlockStateInstance("base:tree_log[default]");
    BlockState woodBlock = this.getBlockStateInstance("base:tree_log[type=bark]");
    BlockState strippedWoodBlock = this.getBlockStateInstance("floating_islands:stripped_tree_log[type=bark]");
    BlockState branchBlockZ = this.getBlockStateInstance("base:tree_log[default,slab_type=verticalPosZ]");
    BlockState branchBlockNegZ = this.getBlockStateInstance("base:tree_log[default,slab_type=verticalNegZ]");
    BlockState branchBlockX = this.getBlockStateInstance("base:tree_log[default,slab_type=verticalPosX]");
    BlockState branchBlockNegX = this.getBlockStateInstance("base:tree_log[default,slab_type=verticalNegX]");
    BlockState magmaBlock = this.getBlockStateInstance("base:magma[default]");
    BlockState magmaSlabBlock = this.getBlockStateInstance("base:magma[default,slab_type=bottom]");
    BlockState cherryLeavesBlock = this.getBlockStateInstance("floating_islands:cherry_leaves[default]");
    BlockState cherryLeavesSlabBlock = this.getBlockStateInstance("floating_islands:cherry_leaves[default,slab_type=bottom]");
    BlockState darkOakLeavesBlock = this.getBlockStateInstance("floating_islands:dark_oak_leaves[default]");
    BlockState palmLeavesBlock = this.getBlockStateInstance("floating_islands:palm_leaves[default]");
    Random random = new Random(seed);
    private SimplexNoise simplexNoise;
    IBlockDataFactory<BlockState> chunkDataFactory = new IBlockDataFactory<BlockState>() {
        public IBlockData<BlockState> createChunkData() {
            return new SingleBlockData<>(FloatingIslandsZoneGenerator.this.airBlock);
        }
    };

    public FloatingIslandsZoneGenerator() {
    }

    public void create() {
        this.simplexNoise = new SimplexNoise(this.seed);
    }

    public String getSaveKey() {
        return "example_mod:floating_islands";
    }

    protected String getName() {
        return "Floating Islands";
    }

    public void generateForChunkColumn(Zone zone, ChunkColumn col) {
        int maxCy = 15;

        for (int cy = 0; cy <= maxCy; ++cy) {
            Chunk chunk = zone.getChunkAtChunkCoords(col.chunkX, cy, col.chunkZ);
            if (chunk == null) {
                chunk = new Chunk(col.chunkX, cy, col.chunkZ);
                chunk.initChunkData(this.chunkDataFactory);
                zone.addChunk(chunk);
                col.addChunk(chunk);
            }

            this.generateForChunk(chunk);
        }

    }

    private void generateForChunk(Chunk chunk) {
        Zone zone = chunk.region.zone;

        for (int localY = 0; localY < CHUNK_WIDTH; ++localY) {
            int globalY = chunk.blockY + localY;
            if (globalY > 0) {
                for (int localX = 0; localX < CHUNK_WIDTH; ++localX) {
                    int globalX = chunk.blockX + localX;
                    for (int localZ = 0; localZ < CHUNK_WIDTH; ++localZ) {
                        int globalZ = chunk.blockZ + localZ;
                        Vector2 horizontalPos = new Vector2(globalX, globalZ);
                        double ocean = ocean(globalX, globalZ);
                        double featureNoise = featureNoise(globalX, globalZ);
                        double crackNoise = crackNoise(globalX, globalZ);
                        if (isTerrain(globalX, globalY, globalZ)) {
                            BlockState subSurface = this.dirtBlock;
                            if (temperature(globalX, globalZ) > 0.66) {
                                if (featureNoise < -0.9) {
                                    subSurface = this.magmaBlock;
                                } else {
                                    subSurface = this.sandBlock;
                                }
                            } else if (ocean > 0.7 && temperature(globalX, globalZ) > -0.5) {
                                subSurface = this.sandBlock;
                            }
                            replaceIfAboveAirOrSelf(zone, this.stoneBasaltBlock, globalX, globalY - 1, globalZ);
                            replaceIfAboveAirOrSelf(zone, this.stoneBasaltBlock, globalX, globalY, globalZ + 1);
                            replaceIfAboveAirOrSelf(zone, this.stoneBasaltBlock, globalX, globalY, globalZ - 1);
                            replaceIfAboveAirOrSelf(zone, this.stoneBasaltBlock, globalX + 1, globalY, globalZ);
                            replaceIfAboveAirOrSelf(zone, this.stoneBasaltBlock, globalX - 1, globalY, globalZ);
                            if (!isTerrain(globalX, globalY + 1, globalZ)) {
                                heightmap.put(horizontalPos, globalY);
                            }
                             if (heightmap.get(horizontalPos) != null && heightmap.get(horizontalPos) == globalY) {
                                 if (ocean > 0.75) {
                                     replaceIfNotWater(zone, this.airBlock, globalX, globalY + 2, globalZ);
                                     replaceIfNotWater(zone, this.airBlock, globalX, globalY + 1, globalZ);
                                     zone.setBlockState(this.waterBlock, globalX, globalY, globalZ);
                                     int minDepth = (int) (globalY - Math.max(0, (ocean-0.75)*100)+1);
                                     for (int depth = globalY; depth >= minDepth; depth--) {
                                         if (zone.getBlockState(globalX, depth, globalZ) == sandBlock) {
                                             zone.setBlockState(this.waterBlock, globalX, depth, globalZ);
                                         }
                                     }
                                 } else {
                                    zone.setBlockState(airBlock, globalX, globalY + 1, globalZ);
                                    if (ocean > 0.7 && temperature(globalX, globalZ) > -0.5) {
                                        zone.setBlockState(this.sandBlock, globalX, globalY, globalZ);
                                        if (random.nextInt(0, 420) <= Math.abs(featureNoise * 2) + 1) {
                                            makePalmTrunk(zone, globalX, globalY + 1, globalZ);
                                        }
                                    } else {
                                        BlockState surface = this.grassBlock;
                                        if (crackNoise > -0.15 && crackNoise < 0.15) {
                                            if (featureNoise < 0.3) {
                                                surface = this.grassSlabBlock;
                                            } else {
                                                surface = this.dirtSlabBlock;
                                            }
                                        }
                                        if (temperature(globalX, globalZ) > 0.66) {
                                            if (featureNoise < -0.9) {
                                                surface = this.magmaSlabBlock;
                                            } else if (crackNoise > -0.15 && crackNoise < 0.15) {
                                                surface = this.sandSlabBlock;
                                            } else {
                                                surface = this.sandBlock;
                                            }
                                        } else if (temperature(globalX, globalZ) < -0.66) {
                                            surface = this.snowBlock;
                                        }
                                        zone.setBlockState(surface, globalX, globalY, globalZ);
                                        if (surface == this.sandBlock && random.nextInt(0, 40) <= Math.abs(featureNoise) + 0.5) {
                                            BlockState cactusType = this.cactusBlockZ;
                                            if (random.nextFloat() < 0.25) {
                                                cactusType = this.cactusBlockNegZ;
                                            } else if (random.nextFloat() < 0.5) {
                                                cactusType = this.cactusBlockX;
                                            } else if (random.nextFloat() < 0.75) {
                                                cactusType = this.cactusBlockNegX;
                                            }
                                            zone.setBlockState(cactusType, globalX, globalY + 1, globalZ);
                                            zone.setBlockState(cactusType, globalX, globalY + 2, globalZ);
                                            if (featureNoise > 0.5) {
                                                zone.setBlockState(cactusType, globalX, globalY + 3, globalZ);
                                                if (featureNoise > 0.8) {
                                                    zone.setBlockState(cactusType, globalX, globalY + 4, globalZ);
                                                    zone.setBlockState(cactusType, globalX, globalY + 5, globalZ);
                                                }
                                            }
                                        } else if (surface == this.grassBlock) {
                                            if (featureNoise < 0.3) {
                                                if (random.nextInt(0, 420) <= Math.abs(featureNoise * 2) + 1) {
                                                    int height = random.nextInt(6, 24);
                                                    if (makeTrunk(height, zone, globalX, globalY, globalZ)) {
                                                        makeCanopy(cherryLeavesBlock, random.nextInt(5, 7), zone, globalX, globalY + height, globalZ);
                                                    }
                                                } else if (random.nextInt(0, 384) <= Math.abs(featureNoise * 2) + 1) {
                                                    int height = 1;
                                                    if (makeTrunk(height, zone, globalX, globalY, globalZ)) {
                                                        makeSphere(darkOakLeavesBlock, 2, zone, globalX, globalY + 1, globalZ);
                                                    }
                                                } else {
                                                    zone.setBlockState(cherryLeavesSlabBlock, globalX, globalY + 1, globalZ);
                                                }
                                            } else if (random.nextInt(0, 172) <= Math.abs(featureNoise * 2) + 1) {
                                                int height = random.nextInt(6, 16);
                                                if (makeTrunk(height, zone, globalX, globalY, globalZ)) {
                                                    makeCanopy(darkOakLeavesBlock, random.nextInt(3, 6), zone, globalX, globalY + height, globalZ);
                                                }
                                            } else if (random.nextInt(0, 48) <= Math.abs(featureNoise * 2) + 1) {
                                                int height = 1;
                                                if (makeTrunk(height, zone, globalX, globalY, globalZ)) {
                                                    makeSphere(darkOakLeavesBlock, 2, zone, globalX, globalY + 1, globalZ);
                                                }
                                            }
                                        }
                                    }
                                }
                            } else {
                                zone.setBlockState(subSurface, globalX, globalY, globalZ);
                            }
                        }
                    }
                }
            }
        }
    }

    private void replaceIfAboveAirOrSelf(Zone zone, BlockState blockState, int x, int y, int z) {
        if ((zone.getBlockState(x, y, z) == null || zone.getBlockState(x, y, z) == airBlock) && (zone.getBlockState(x, y-1, z) == null || zone.getBlockState(x, y-1, z) == airBlock || zone.getBlockState(x, y-1, z) == blockState || zone.getBlockState(x, y+1, z) == blockState)) {
            zone.setBlockState(blockState, x, y, z);
        }
    }

    private void replaceIfNotWater(Zone zone, BlockState blockState, int x, int y, int z) {
        if (zone.getBlockState(x, y, z) != waterBlock) {
            zone.setBlockState(blockState, x, y, z);
        }
    }

    private void replaceIfNotAboveWater(Zone zone, BlockState blockState, int x, int y, int z) {
        if (zone.getBlockState(x, y, z) != waterBlock && zone.getBlockState(x, y-1, z) != waterBlock && zone.getBlockState(x, y-2, z) != waterBlock) {
            zone.setBlockState(blockState, x, y, z);
        }
    }

    private void makePalmTrunk(Zone zone, int x, int y, int z) {
        List<Vector3> canopiesToPlace = new ArrayList<>();
        List<Vector3> placesToPlace = new ArrayList<>();
        for (int trunks = 0; trunks <= random.nextInt(0, 2)-0.1; trunks++) {
            int offsetX = x;
            int offsetZ = z;
            int maxHeight = y+random.nextInt(8, 22);
            for (int height = y; height <= maxHeight; height++) {
                float bendFactor = ((float) maxHeight /height)*2F;
                placesToPlace.add(new Vector3(offsetX, height, offsetZ));
                if (height == maxHeight) {
                    canopiesToPlace.add(new Vector3(offsetX, height+1, offsetZ));
                } else if (height < maxHeight-4) {
                    if (trunks == 0) {
                        if (random.nextInt(0, 5) < 3-bendFactor) {
                            offsetX += 1;
                        }
                        if (random.nextInt(0, 5) < 3-bendFactor) {
                            offsetZ += 1;
                        }
                    } else {
                        if (random.nextInt(0, 5) < 4-bendFactor) {
                            offsetX -= 1;
                        }
                        if (random.nextInt(0, 5) < 4-bendFactor) {
                            offsetZ -= 1;
                        }
                    }
                    placesToPlace.add(new Vector3(offsetX, height, offsetZ));
                }
            }
        }
        boolean intersecting = false;
        for (Vector3 vector3 : placesToPlace) {
            if (zone.getBlockState(vector3) != null && zone.getBlockState(vector3) != airBlock) {
                intersecting = true;
            }
        }
        if (!intersecting) {
            for (Vector3 pos : placesToPlace) {
                zone.setBlockState(strippedWoodBlock, (int) pos.x, (int) pos.y, (int) pos.z);
            }
            for (Vector3 pos : canopiesToPlace) {
                makePalmCanopy(palmLeavesBlock, zone, (int) pos.x, (int) pos.y, (int) pos.z);
            }
        }
    }

    private void makePalmCanopy(BlockState leaves, Zone zone, int x, int y, int z) {
        replaceAir(zone, leaves, x, y, z);
        replaceAir(zone, leaves, x+1, y, z);
        replaceAir(zone, leaves, x-1, y, z);
        replaceAir(zone, leaves, x, y, z+1);
        replaceAir(zone, leaves, x, y, z-1);
        replaceAir(zone, leaves, x+2, y, z);
        replaceAir(zone, leaves, x-2, y, z);
        replaceAir(zone, leaves, x, y, z+2);
        replaceAir(zone, leaves, x, y, z-2);

        y--;

        replaceAir(zone, leaves, x+1, y, z);
        replaceAir(zone, leaves, x-1, y, z);
        replaceAir(zone, leaves, x, y, z+1);
        replaceAir(zone, leaves, x, y, z-1);
        replaceAir(zone, leaves, x+2, y, z);
        replaceAir(zone, leaves, x-2, y, z);
        replaceAir(zone, leaves, x, y, z+2);
        replaceAir(zone, leaves, x, y, z-2);
        replaceAir(zone, leaves, x+3, y, z);
        replaceAir(zone, leaves, x-3, y, z);
        replaceAir(zone, leaves, x, y, z+3);
        replaceAir(zone, leaves, x, y, z-3);
        replaceAir(zone, leaves, x-1, y, z-1);
        replaceAir(zone, leaves, x-1, y, z+1);
        replaceAir(zone, leaves, x+1, y, z+1);
        replaceAir(zone, leaves, x+1, y, z-1);
        replaceAir(zone, leaves, x-2, y, z-1);
        replaceAir(zone, leaves, x-2, y, z+1);
        replaceAir(zone, leaves, x+2, y, z+1);
        replaceAir(zone, leaves, x+2, y, z-1);
        replaceAir(zone, leaves, x-1, y, z-2);
        replaceAir(zone, leaves, x-1, y, z+2);
        replaceAir(zone, leaves, x+1, y, z+2);
        replaceAir(zone, leaves, x+1, y, z-2);

        y--;

        replaceAir(zone, leaves, x-1, y, z-1);
        replaceAir(zone, leaves, x-1, y, z+1);
        replaceAir(zone, leaves, x+1, y, z+1);
        replaceAir(zone, leaves, x+1, y, z-1);
        replaceAir(zone, leaves, x-2, y, z);
        replaceAir(zone, leaves, x-2, y, z-1);
        replaceAir(zone, leaves, x-2, y, z+1);
        replaceAir(zone, leaves, x+2, y, z);
        replaceAir(zone, leaves, x+2, y, z-1);
        replaceAir(zone, leaves, x+2, y, z+1);
        replaceAir(zone, leaves, x, y, z-2);
        replaceAir(zone, leaves, x-1, y, z-2);
        replaceAir(zone, leaves, x+1, y, z-2);
        replaceAir(zone, leaves, x, y, z+2);
        replaceAir(zone, leaves, x-1, y, z+2);
        replaceAir(zone, leaves, x+1, y, z+2);
        replaceAir(zone, leaves, x-2, y, z-2);
        replaceAir(zone, leaves, x-2, y, z+2);
        replaceAir(zone, leaves, x+2, y, z+2);
        replaceAir(zone, leaves, x+2, y, z-2);
        replaceAir(zone, leaves, x, y, z+3);
        replaceAir(zone, leaves, x, y, z+4);
        replaceAir(zone, leaves, x, y, z-3);
        replaceAir(zone, leaves, x, y, z-4);
        replaceAir(zone, leaves, x+3, y, z);
        replaceAir(zone, leaves, x+4, y, z);
        replaceAir(zone, leaves, x-3, y, z);
        replaceAir(zone, leaves, x-4, y, z);

        y--;

        replaceAir(zone, leaves, x-1, y, z-1);
        replaceAir(zone, leaves, x-1, y, z+1);
        replaceAir(zone, leaves, x+1, y, z+1);
        replaceAir(zone, leaves, x+1, y, z-1);
        replaceAir(zone, leaves, x-2, y, z-2);
        replaceAir(zone, leaves, x-2, y, z+2);
        replaceAir(zone, leaves, x+2, y, z+2);
        replaceAir(zone, leaves, x+2, y, z-2);
        replaceAir(zone, leaves, x, y, z+2);
        replaceAir(zone, leaves, x, y, z+3);
        replaceAir(zone, leaves, x, y, z+4);
        replaceAir(zone, leaves, x, y, z-2);
        replaceAir(zone, leaves, x, y, z-3);
        replaceAir(zone, leaves, x, y, z-4);
        replaceAir(zone, leaves, x+2, y, z);
        replaceAir(zone, leaves, x+3, y, z);
        replaceAir(zone, leaves, x+4, y, z);
        replaceAir(zone, leaves, x-2, y, z);
        replaceAir(zone, leaves, x-3, y, z);
        replaceAir(zone, leaves, x-4, y, z);

        y--;

        replaceAir(zone, leaves, x-1, y, z-1);
        replaceAir(zone, leaves, x-1, y, z+1);
        replaceAir(zone, leaves, x+1, y, z+1);
        replaceAir(zone, leaves, x+1, y, z-1);
        replaceAir(zone, leaves, x-2, y, z-2);
        replaceAir(zone, leaves, x-2, y, z+2);
        replaceAir(zone, leaves, x+2, y, z+2);
        replaceAir(zone, leaves, x+2, y, z-2);
        replaceAir(zone, leaves, x+4, y, z);
        replaceAir(zone, leaves, x-4, y, z);
        replaceAir(zone, leaves, x, y, z+4);
        replaceAir(zone, leaves, x, y, z-4);

        y--;

        replaceAir(zone, leaves, x-2, y, z-2);
        replaceAir(zone, leaves, x-2, y, z+2);
        replaceAir(zone, leaves, x+2, y, z+2);
        replaceAir(zone, leaves, x+2, y, z-2);
    }

    private void makeCanopy(BlockState leaves, int radius, Zone zone, int globalX, int globalY, int globalZ) {
        makeSphere(leaves, radius-2, zone, globalX, globalY-2, globalZ);
        makeSphere(leaves, radius, zone, globalX, globalY, globalZ);
        makeSphere(leaves, radius-1, zone, (int) (globalX+random.nextFloat(-0.25F*radius, 0.25F*radius)), globalY+1, (int) (globalZ+random.nextFloat(-0.25F*radius, 0.25F*radius)));
    }

    private void makeSphere(BlockState leaves, int radius, Zone zone, int globalX, int globalY, int globalZ) {
        for (int x = globalX-radius; x <= globalX+radius-1; x++) {
            for (int y = (int) (globalY-(radius*0.33)); y <= globalY+(radius*0.75); y++) {
                for (int z = globalZ-radius+1; z <= globalZ+radius; z++) {
                    double distance = Math.abs(globalX-x)+Math.abs(globalY-y)+Math.abs(globalZ-z);
                    if (distance < radius*1.5) {
                        replaceAir(zone, leaves, x, y, z);
                    }
                }
            }
        }
    }

    private void replaceAir(Zone zone, BlockState block, int x, int y, int z) {
        if (zone.getBlockState(x, y, z) == null || zone.getBlockState(x, y, z) == airBlock || zone.getBlockState(x, y, z) == cherryLeavesSlabBlock) {
            zone.setBlockState(block, x, y, z);
        }
    }

    private boolean makeTrunk(int height, Zone zone, int globalX, int globalY, int globalZ) {
        List<Vector3> placesToPlace = new ArrayList<>();
        int xTilt = 1;
        if (random.nextBoolean()) {
            xTilt = -1;
        }
        int zTilt = 1;
        if (random.nextBoolean()) {
            zTilt = -1;
        }
        for (int i = 1; i <= height; i++) {
            placesToPlace.add(new Vector3(globalX, globalY+i, globalZ));
            if (height >= 9) {
                if (i <= height/3) {
                    placesToPlace.add(new Vector3(globalX-xTilt, globalY+i-1, globalZ-zTilt));
                } else if (i >= height/2+1) {
                    placesToPlace.add(new Vector3(globalX+xTilt, globalY+i, globalZ+zTilt));
                }
            }
        }
        boolean intersecting = false;
        for (Vector3 vector3 : placesToPlace) {
            if (zone.getBlockState(vector3) != null && zone.getBlockState(vector3) != airBlock && zone.getBlockState(vector3) != darkOakLeavesBlock) {
                intersecting = true;
            }
        }
        if (!intersecting) {
            for (Vector3 pos : placesToPlace) {
                zone.setBlockState(woodBlock, (int) pos.x, (int) pos.y, (int) pos.z);
            }
            if (height > 2) {
                int branchAltitude = globalY + random.nextInt(2, height);
                float branchChance = random.nextFloat();
                if (branchChance < 0.2) {
                    zone.setBlockState(branchBlockZ, globalX, branchAltitude, globalZ - 1);
                } else if (branchChance < 0.4) {
                    zone.setBlockState(branchBlockNegZ, globalX, branchAltitude, globalZ + 1);
                } else if (branchChance < 0.6) {
                    zone.setBlockState(branchBlockX, globalX - 1, branchAltitude, globalZ);
                } else if (branchChance < 0.8) {
                    zone.setBlockState(branchBlockNegX, globalX + 1, branchAltitude, globalZ);
                }
            }
            return true;
        }
        return false;
    }

    private boolean isTerrain(int globalX, int globalY, int globalZ) {
        return Math.min(0.5 - (simplexNoise.noise3_XZBeforeY(globalX * 0.02F, globalY * 0.005F, globalZ * 0.02F) + FloatingIslandsMath.gradient(globalY, 64, 192, -1, 1.5F)),
                simplexNoise.noise3_XZBeforeY(globalX * 0.0024F, globalY * 0.0016F, globalZ * 0.0024F) + (FloatingIslandsMath.gradient(globalY, 128, 256, 0.75F, 0.5F) - (2 * (0.1 + FloatingIslandsMath.gradient(globalY, 69, 159, 0.76F, 0F))))) > 0;
    }

    private double ocean(int globalX, int globalZ) {
        return simplexNoise.noise2(globalX * 0.001F, globalZ * 0.001F);
    }

    private double temperature(int globalX, int globalZ) {
        return simplexNoise.noise2(globalX * 0.0006F, globalZ * 0.0006F);
    }

    private double featureNoise(int globalX, int globalZ) {
        return simplexNoise.noise2(globalX * 0.0025F, globalZ * 0.0025F);
    }

    private double crackNoise(int globalX, int globalZ) {
        return simplexNoise.noise2(globalX * 0.05F, globalZ * 0.05F);
    }
}
