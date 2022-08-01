package kmaput.sculk_extras.util;

import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.phys.Vec3;

public record DQuat(double r, double x, double y, double z) {
    public static final DQuat ONE = new DQuat(1, 0, 0, 0);

    private static final DQuat[] TO_AXIS_ROTATIONS = {
            DQuat.rotation(Math.PI, 0, 0),
            DQuat.rotation(0, 0, 0),
            DQuat.rotation(Math.PI*0.5, Math.PI, 0),
            DQuat.rotation(Math.PI*0.5, 0, 0),
            DQuat.rotation(Math.PI*0.5, Math.PI*1.5, 0),
            DQuat.rotation(Math.PI*0.5, Math.PI*0.5, 0)
    };

    public static DQuat from(double real, Vec3 vec) {
        return new DQuat(real, vec.x, vec.y, vec.z);
    }

    public static DQuat from(Vec3 vec) {
        return new DQuat(0, vec.x, vec.y, vec.z);
    }

    public static DQuat from(Vec3i vec) {
        return new DQuat(0, vec.getX(), vec.getY(), vec.getZ());
    }

    public static DQuat axisRotation(double angle, Vec3 axis) {
        double s = Math.sin(angle/2);
        return new DQuat(Math.cos(angle/2), s*axis.x, s*axis.y, s*axis.z);
    }

    public static DQuat rotation(double yaw, double pitch, double roll) {
        double
                cy = Math.cos(yaw/2),
                sy = Math.sin(yaw/2),
                cp = Math.cos(pitch/2),
                sp = Math.sin(pitch/2),
                cr = Math.cos(roll/2),
                sr = Math.sin(roll/2);
        return new DQuat(
                cy*cp*cr + sy*sp*sr,
                sy*cp*cr - cy*sp*sr,
                cy*sp*cr + sy*cp*sr,
                cy*cp*sr - sy*sp*cr
        );
    }

    public static DQuat rotation(Direction direction) {
        if (direction == null) return ONE;
        return TO_AXIS_ROTATIONS[direction.ordinal()];
    }

    public static Vec3 toVec(DQuat quat) {
        return new Vec3(quat.x, quat.y, quat.z);
    }

    public static DQuat add(DQuat lhs, DQuat rhs) {
        return new DQuat(lhs.r + rhs.r, lhs.x + rhs.x, lhs.y + rhs.y, lhs.z + rhs.z);
    }

    public static DQuat add(DQuat... many) {
        DQuat ret = ONE;
        for (DQuat quat : many) {
            ret = add(ret, quat);
        }
        return ret;
    }

    public static DQuat mul(DQuat lhs, DQuat rhs) {
        return new DQuat(
                lhs.r * rhs.r - lhs.x * rhs.x - lhs.y * rhs.y - lhs.z * rhs.z,
                lhs.r * rhs.x + lhs.x * rhs.r + lhs.y * rhs.z - lhs.z * rhs.y,
                lhs.r * rhs.y + lhs.y * rhs.r + lhs.z * rhs.x - lhs.x * rhs.z,
                lhs.r * rhs.z + lhs.z * rhs.r + lhs.x * rhs.y - lhs.y * rhs.x
        );
    }

    public static DQuat mul(DQuat... many) {
        DQuat ret = ONE;
        for (DQuat quat : many) {
            ret = mul(ret, quat);
        }
        return ret;
    }

    public static DQuat scale(DQuat quat, double scalar) {
        return new DQuat(quat.r*scalar, quat.x*scalar, quat.y*scalar, quat.z*scalar);
    }

    public static DQuat negate(DQuat quat) {
        return new DQuat(-quat.r, -quat.x, -quat.y, -quat.z);
    }

    public static DQuat subtract(DQuat lhs, DQuat rhs) {
        return new DQuat(lhs.r - rhs.r, lhs.x - rhs.x, lhs.y - rhs.y, lhs.z - rhs.z);
    }

    public static DQuat conj(DQuat quat) {
        return new DQuat(quat.r, -quat.x, -quat.y, -quat.z);
    }

    public static double lenSq(DQuat quat) {
        return quat.r*quat.r+quat.x*quat.x+quat.y*quat.y+quat.z*quat.z;
    }

    public static double len(DQuat quat) {
        return Math.sqrt(lenSq(quat));
    }

    public static DQuat unit(DQuat quat) {
        double lenInv = 1/len(quat);
        return new DQuat(quat.r*lenInv, quat.x*lenInv, quat.y*lenInv, quat.z*lenInv);
    }

    public static DQuat inv(DQuat quat) {
        double lenSqInv = 1/lenSq(quat);
        return new DQuat(quat.r*lenSqInv, -quat.x*lenSqInv, -quat.y*lenSqInv, -quat.z*lenSqInv);
    }

    /**
     * @param rotor Must be unit quaternion
     */
    public static Vec3 rotate(Vec3 vec, DQuat rotor) {
        return toVec(mul(rotor, DQuat.from(vec), conj(rotor)));
    }
}
