package com.vicious.viciouslibkit.util;

import com.vicious.viciouslib.util.interfaces.TriConsumer;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;

public class WorldUtil {
    public static void spawnParticlesAround(Location l, World w, Particle p, int count, double osx, double osy, double osz, double distFrom, int numOfParticleGroups){
        double roc = (1+distFrom*2)/numOfParticleGroups;
        l=centerParticleLocation(l);
        forXYZHollow(l.getX()-0.5-distFrom,l.getX()+0.5+distFrom,l.getY()-0.5-distFrom, l.getY()+0.5+distFrom,l.getZ()-0.5-distFrom, l.getZ()+0.5+distFrom,roc,roc,roc,(x,y,z)->{
            w.spawnParticle(p,x,y,z,count,osx,osy,osz);
        });
    }
    public static <T> void spawnParticlesAround(Location l, World w, Particle p, int count, double osx, double osy, double osz, double distFrom, int numOfParticleGroups, T dat){
        double roc = (1+distFrom*2)/numOfParticleGroups;
        l=centerParticleLocation(l);
        forXYZHollow(l.getX()-0.5-distFrom,l.getX()+0.5+distFrom,l.getY()-0.5-distFrom, l.getY()+0.5+distFrom,l.getZ()-0.5-distFrom, l.getZ()+0.5+distFrom,roc,roc,roc,(x,y,z)->{
            w.spawnParticle(p,x,y,z,count,osx,osy,osz,dat);
        });
    }
    public static <T> void spawnHoveringParticlesAround(Location l, World w, Particle p, int count, double osx, double osy, double osz, double distFrom, int numOfParticleGroups, T dat){
        double roc = (1+distFrom*2)/numOfParticleGroups;
        l=centerParticleLocation(l);
        forXYZHollow(l.getX()-0.5-distFrom,l.getX()+0.5+distFrom,l.getY()-0.5-distFrom, l.getY()+0.5+distFrom,l.getZ()-0.5-distFrom, l.getZ()+0.5+distFrom,roc,roc,roc,(x,y,z)->{
            for (int i = 0; i < count; i++) {
                w.spawnParticle(p,x+randOffset(osx),y+randOffset(osy),z+randOffset(osz),0,0,0,0,dat);
            }
        });
    }
    public static <T> void spawnHoveringParticlesAround(Location l, World w, Particle p, int count, double osx, double osy, double osz, double distFrom, int numOfParticleGroups){
        double roc = (1.0+distFrom*2.0)/(double)numOfParticleGroups;
        l=centerParticleLocation(l);
        forXYZHollow(l.getX()-0.5-distFrom,l.getX()+0.5+distFrom,l.getY()-0.5-distFrom, l.getY()+0.5+distFrom,l.getZ()-0.5-distFrom, l.getZ()+0.5+distFrom,roc,roc,roc,(x,y,z)->{
            for (int i = 0; i < count; i++) {
                w.spawnParticle(p,x+randOffset(osx),y+randOffset(osy),z+randOffset(osz),0,0,0,0);
            }
        });
    }
    public static void forXYZ(double x1, double x2, double y1, double y2, double z1, double z2, double xroc, double yroc, double zroc, TriConsumer<Double,Double,Double> cons){
        for(double x = x1; x <= x2; x+=xroc){
            for(double y = y1; y <= y2; y+=yroc){
                for(double z = z1; z <= z2; z+=zroc){
                    cons.accept(x,y,z);
                }
            }
        }
    }
    public static void forXYZHollow(double x1, double x2, double y1, double y2, double z1, double z2, double xroc, double yroc, double zroc, TriConsumer<Double,Double,Double> cons){
        for(double x = x1; x <= x2; x+=xroc){
            for(double y = y1; y <= y2; y+=yroc){
                for(double z = z1; z <= z2; z+=zroc){
                    if(!isWall(x,x1,x2,y,y1,y2,z,z1,z2,xroc,yroc,zroc)) continue;
                    cons.accept(x,y,z);
                }
            }
        }
    }

    public static boolean isWall(double x, double x1, double x2, double y, double y1, double y2, double z, double z1, double z2,double xthick, double ythick, double zthick) {
        return (x == x1 || x >= x2-xthick) || (y == y1 || y >= y2-ythick) || (z == z1 || z >= z2-zthick);
    }

    private static double randOffset(double offsetMax) {
        return Math.random()*offsetMax;
    }

    /**
     * For some dumb reason, bukkit spawns particles on the north-west-bottom corner of a block when given a blockpos.
     */
    public static Location centerParticleLocation(Location in){
        return in.add(0.5,0.5,0.5);
    }
}
