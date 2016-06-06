/**
 * Author: Radim KUBIŠ, xkubis03
 * Name: Cell.java
 *
 * Class for BTS cell.
 */

package cz.vutbr.fit.xkubis03.sen;

import android.location.Location;

public class Cell {

    // Attributes **********************************************************************************

    /* ID of cell in DB */
    protected long id;
    /* Location area code */
    protected int lac;
    /* Cell ID */
    protected int cid;
    /* Signal strength */
    protected int strength;
    /* GPS location of cell */
    protected Location location;
    /* Type of cell */
    protected CellType type;

    // Constructors ********************************************************************************

    public Cell() {
        setLac(0);
        setCid(0);
        setStrength(0);
        setLocation(0, 0);
        setType(CellType.UNKNOWN);
    }

    public Cell(int lac, int cid) {
        setLac(lac);
        setCid(cid);
        setStrength(0);
        setLocation(0, 0);
        setType(CellType.UNKNOWN);
    }

    public Cell(int lac, int cid, int strength) {
        setLac(lac);
        setCid(cid);
        setStrength(strength);
        setLocation(0, 0);
        setType(CellType.UNKNOWN);
    }

    public Cell(int lac, int cid, int strength, Location location) {
        setLac(lac);
        setCid(cid);
        setStrength(strength);
        setLocation(location);
        setType(CellType.UNKNOWN);
    }

    public Cell(int lac, int cid, int strength, double lat, double lon) {
        setLac(lac);
        setCid(cid);
        setStrength(strength);
        setLocation(lat, lon);
        setType(CellType.UNKNOWN);
    }

    public Cell(int lac, int cid, int strength, double lat, double lon, CellType type) {
        setLac(lac);
        setCid(cid);
        setStrength(strength);
        setLocation(lat, lon);
        setType(type);
    }

    // Getters and setters *************************************************************************

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getLac() {
        return lac;
    }

    public void setLac(int lac) {
        this.lac = lac;
    }

    public int getCid() {
        return cid;
    }

    public void setCid(int cid) {
        this.cid = cid;
    }

    public int getStrength() {
        return strength;
    }

    public void setStrength(int strength) {
        this.strength = strength;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public void setLocation(double lat, double lon) {
        this.location = new Location("xkubis03");
        this.location.setLatitude(lat);
        this.location.setLongitude(lon);
    }

    public Location getLocation() {
        return this.location;
    }

    public CellType getType() {
        return type;
    }

    public void setType(CellType type) {
        this.type = type;
    }

    // Others **************************************************************************************

    public String toString() {
        return "[" + type + "," + lac + "," + cid + "," + strength + "," + location.getLatitude() + "," + location.getLongitude() + "]";
    }
}
