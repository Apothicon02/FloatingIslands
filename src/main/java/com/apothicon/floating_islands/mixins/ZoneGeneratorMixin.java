package com.apothicon.floating_islands.mixins;

import com.apothicon.floating_islands.worldgen.FloatingIslandsZoneGenerator;
import finalforeach.cosmicreach.worldgen.ZoneGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.apothicon.floating_islands.FloatingIslands.isFloatingIslandsRegistered;

@Mixin(ZoneGenerator.class)
public class ZoneGeneratorMixin {

    @Inject(method = "registerZoneGenerators", at = @At("TAIL"))
    private static void injected(CallbackInfo ci) {
        if (isFloatingIslandsRegistered == false) {
            isFloatingIslandsRegistered = true;
            ZoneGenerator.registerZoneGenerator(new FloatingIslandsZoneGenerator());
        }
    }
}
