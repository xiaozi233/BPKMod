package cn.xiaozi0721.bpk.mixin.interfaces;

public interface ILerpSneakCameraEntity {
    float BPKMod$getLastCameraY();
    float BPKMod$getCameraY();
    void BPKMod$updateCameraHeight(double tickDelta);
}
