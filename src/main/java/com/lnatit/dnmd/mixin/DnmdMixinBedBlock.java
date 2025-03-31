package com.lnatit.dnmd.mixin;

import com.lnatit.dnmd.DisturbNotMyDream;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.BedBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BedBlock.class)
public class DnmdMixinBedBlock
{
    @Inject(
            method = "lambda$useWithoutItem$1",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/player/Player;displayClientMessage(Lnet/minecraft/network/chat/Component;Z)V"
            )
    )
    private static void onSleepBlocked(Player player, Player.BedSleepingProblem bedSleepingProblem, CallbackInfo ci) {
        if (bedSleepingProblem == Player.BedSleepingProblem.NOT_SAFE && player instanceof ServerPlayer serverPlayer)
        {
            DisturbNotMyDream.Server.addGrumpy(serverPlayer);
        }
    }
}
