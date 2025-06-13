package me.comu.overrides.ducks;

public interface PlayerMovePacketDuck {
    void setX(double x);
    void setY(double y);
    void setZ(double z);
    void setYaw(float yaw);
    void setPitch(float pitch);
}
