package org.verschluesselung;

import jpcap.JpcapCaptor;
import jpcap.NetworkInterface;
import jpcap.PacketReceiver;
import jpcap.packet.Packet;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Sniffer implements PacketReceiver {
    static int protocol;
    static JpcapCaptor jpcap;

    public static void main(String[] args) throws Exception {
        NetworkInterface[] devices = JpcapCaptor.getDeviceList();
            jpcap = JpcapCaptor.openDevice(devices[Integer.parseInt(args[0])], 2000, true, 20);
                //jpcap.setFilter("", true);
            jpcap.loopPacket(-1, new Sniffer());
    }

    @Override
    public void receivePacket(Packet packet) {
        String data = new String(packet.data);
        String pack = new String(packet.toString());
        System.out.println("-----------------------START------------------------");
        System.out.println(pack);
        System.out.println(data);
        System.out.println("-----------------------END------------------------");
    }

    private String getDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }
}

