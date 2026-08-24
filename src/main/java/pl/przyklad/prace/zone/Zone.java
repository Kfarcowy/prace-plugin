package pl.przyklad.prace.zone;

import org.bukkit.Location;
import pl.przyklad.prace.model.JobType;

public class Zone {

    private final String name;
    private final JobType jobType;
    private final String world;
    private final int x1, y1, z1;
    private final int x2, y2, z2;

    public Zone(String name, JobType jobType, String world, int x1, int y1, int z1, int x2, int y2, int z2) {
        this.name = name;
        this.jobType = jobType;
        this.world = world;
        this.x1 = Math.min(x1, x2);
        this.x2 = Math.max(x1, x2);
        this.y1 = Math.min(y1, y2);
        this.y2 = Math.max(y1, y2);
        this.z1 = Math.min(z1, z2);
        this.z2 = Math.max(z1, z2);
    }

    public String getName() {
        return name;
    }

    public JobType getJobType() {
        return jobType;
    }

    public String getWorld() {
        return world;
    }

    public int getX1() { return x1; }
    public int getY1() { return y1; }
    public int getZ1() { return z1; }
    public int getX2() { return x2; }
    public int getY2() { return y2; }
    public int getZ2() { return z2; }

    public boolean contains(Location loc) {
        if (loc.getWorld() == null || !loc.getWorld().getName().equals(world)) {
            return false;
        }
        int x = loc.getBlockX();
        int y = loc.getBlockY();
        int z = loc.getBlockZ();
        return x >= x1 && x <= x2 && y >= y1 && y <= y2 && z >= z1 && z <= z2;
    }
}
