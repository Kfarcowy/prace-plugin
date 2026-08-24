package pl.przyklad.prace.model;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerJobData {

    private final UUID uuid;
    private JobType job; // null = brak pracy

    private double earnedMoney;

    private int minerProgress;   // gornik: 0-30 (stone group)
    private int drwalProgress;   // drwal: 0-15 (log group)

    // farmer: osobny licznik na kazdy rodzaj rosliny
    private final Map<String, Integer> farmerProgress = new HashMap<>();

    public PlayerJobData(UUID uuid) {
        this.uuid = uuid;
    }

    public UUID getUuid() {
        return uuid;
    }

    public JobType getJob() {
        return job;
    }

    public void setJob(JobType job) {
        this.job = job;
    }

    public boolean hasJob() {
        return job != null;
    }

    public double getEarnedMoney() {
        return earnedMoney;
    }

    public void addEarnedMoney(double amount) {
        this.earnedMoney += amount;
    }

    public void setEarnedMoney(double earnedMoney) {
        this.earnedMoney = earnedMoney;
    }

    public int getMinerProgress() {
        return minerProgress;
    }

    public void setMinerProgress(int minerProgress) {
        this.minerProgress = minerProgress;
    }

    public int getDrwalProgress() {
        return drwalProgress;
    }

    public void setDrwalProgress(int drwalProgress) {
        this.drwalProgress = drwalProgress;
    }

    public int getFarmerProgress(String crop) {
        return farmerProgress.getOrDefault(crop, 0);
    }

    public void setFarmerProgress(String crop, int value) {
        farmerProgress.put(crop, value);
    }

    public Map<String, Integer> getFarmerProgressMap() {
        return farmerProgress;
    }
}
