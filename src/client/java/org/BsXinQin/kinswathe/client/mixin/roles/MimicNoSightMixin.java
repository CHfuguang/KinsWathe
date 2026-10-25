package org.BsXinQin.kinswathe.client.mixin.roles;

import dev.doctor4t.wathe.api.Role;
import dev.doctor4t.wathe.cca.GameWorldComponent;
import dev.doctor4t.wathe.client.WatheClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WatheClient.class)
public class MimicNoSightMixin {

    private static final Identifier MIMIC_ID = Identifier.of("noellesroles", "mimic");

    @Inject(method = "isInstinctEnabled", at = @At("RETURN"), cancellable = true)
    private static void mimic$disableInstinct(CallbackInfoReturnable<Boolean> cir) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null) return;

        GameWorldComponent gameWorld = GameWorldComponent.KEY.get(mc.player.getWorld());
        Role role = gameWorld.getRole(mc.player);
        if (role == null) return;

        if (MIMIC_ID.equals(role.identifier())) {
            cir.setReturnValue(false);
        }
    }
}
