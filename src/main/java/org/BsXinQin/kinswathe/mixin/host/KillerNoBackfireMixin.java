package org.BsXinQin.kinswathe.mixin.host;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import dev.doctor4t.wathe.cca.GameWorldComponent;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;
import org.BsXinQin.kinswathe.KinsWatheConfig;
import org.BsXinQin.kinswathe.KinsWatheRoles;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(targets = "dev.doctor4t.wathe.network.GunShootPayload$Receiver", priority = 500)
public class KillerNoBackfireMixin {

    @WrapOperation(
            method = "receive",
            at = @At(value = "INVOKE",
                    target = "Ldev/doctor4t/wathe/util/Scheduler;schedule(Ljava/lang/Runnable;I)V")
    )
    private void kinswathe$skipRevolverDrop(Runnable task, int delay, Operation<Void> original,
                                            @Local(argsOnly = true) ServerPlayNetworking.Context context) {
        ServerPlayerEntity shooter = context.player();
        GameWorldComponent gw = GameWorldComponent.KEY.get(shooter.getWorld());

        boolean villain = gw.isRole(shooter, KinsWatheRoles.LICENSED_VILLAIN);
        boolean killer = KinsWatheConfig.HANDLER.instance().EnableWatheModify
                && KinsWatheConfig.HANDLER.instance().PreventKillerDropRevolver
                && gw.canUseKillerFeatures(shooter);

        System.out.println("[kinswathe-fix] hook fired: villain=" + villain
                + ", killer=" + killer + ", shooter=" + shooter.getName().getString());

        if ((villain || killer) && shooter.isAlive() && !shooter.isSpectator()) {
            return;   // 不调用 original → 不调度掉落
        }
        original.call(task, delay);
    }
}
