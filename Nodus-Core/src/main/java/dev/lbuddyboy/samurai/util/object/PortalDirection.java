package dev.lbuddyboy.samurai.util.object;

public enum PortalDirection {

    NORTH_SOUTH((byte) 2, (byte) 0),
    EAST_WEST((byte) 1);

    private byte[] portalData;

    PortalDirection(byte... portalData) {
        this.portalData = portalData;
    }

    public static PortalDirection fromPortalData(byte portalData) {
        for (PortalDirection portalDirection : values()) {
            for (byte data : portalDirection.portalData) {
                if (data == portalData) {
                    return (portalDirection);
                }
            }
        }

        return (null);
    }

}