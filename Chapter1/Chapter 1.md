# Pi 'N' Mesh : Setting up the Mesh Infrastructure
The purpose of this chapter is to create a standalone network that is independent of any external Router for it's operation.
This is the **Mesh**.

The various components of the Mesh are :
* Master - only 1 per Mesh.
This is the machine which supplies the required infrastructure to support the Mesh : DHCP, DNS, etc. For this Chapter, we won't touch on the 'etc.'.
I refer to this Node as 'mesh-master', which is it's hostname.
This Chapter provides instructions to set up a Pi as the Master.
I used an A+ for this as the extra processing power and multi-core operation is more suited to the type of operations we will be doing.
* Client - 1 -> n per Mesh.
This is any machine which is configured to connect to the Master. This chapter provides instructions for setting up any Pi as a Client. The Master can be the client too.
* Bridge - external network connectivity.
This is a machine with 2 network adapters. 1 connects to the external network (usually via a Router) and the other to the Mesh. This chapter provides instructions for setting up a Pi B+ as a Bridge.
I refer to this Node as 'mesh-bridge', which is it's hostname.
    
When it is connected to the external network (or, more properly, when it is connected to via a/the Bridge), machines 
inside the mesh will be able to access the network, which makes installation and configuration 
much easier.

Note that all this is just 1 possible configuration, which I am using. Other flavours 
are entirely acceptable.
My first configuration was a single Pi Zero as Master and Client, and a PC as the Bridge.

Before you start :
* Identify your Router access configuration - usually SSID and Password.
* Pick your Mesh SSID and Password. This is how all other systems will access the Mesh
* Ensure you know how to create an SD card to boot from (we'll be using Raspbian Stretch/Buster Lite).
* Pick an IP address range to use.  I use '10.19.7.x', and reserve the first few for 
  static IP's. It must not be the same as your Router's.
* Choose a naming scheme for your Mesh nodes (each Pi is a Node). I use 'mesh-xxx'.

# Initialising the Base System
~~~
Create SD card from image - I use Raspbian Stretch Lite (Buster for the Bridge).
Copy 'ssh' to /boot.
For 'Master', modify/copy 'wpa_supplicant.conf.local' to /boot/wpa_supplicant.conf.
All others (including Bridge), modify/copy 'wpa_supplicant.conf.mesh' to /boot/wpa_supplicant.conf.
~~~

~~~
Boot from the SDCard.
The IP address will be provided by the Router configured above.
Find the IP address (via the Router - it'll be called 'raspberrypi').
When initialising the Master, the Router will be the local network.
For the Bridge and any Clients, the IP address will be provided by the Master (which
must be configured and running as an AP).
~~~

Login via (e.g.) ssh : 
~~~
ssh pi@xxx.xxx.xxx.xxx
(password is raspberry)
~~~

Then ..
~~~
pi@raspberrypi:~ $ sudo bash
~~~

Initial config
~~~
root@mesh-master:/home/pi# raspi-config
~~~

~~~
Change password and hostname (don't bother to reboot yet)
Enable i2c/camera etc as required
~~~
Update the system (you should always be up to date)
~~~
root@mesh-master:/home/pi# apt-get update
root@mesh-master:/home/pi# apt-get -y upgrade
~~~

Reboot

Your pi will now be accessible from your Router (if you set it up as Master) or 
from the Bridge or Master if it was a client.
If your development machine has a spare WiFi connector, and the Master is configured 
and running as an AP, you can connect directly to the 'MeshAccess' network and access 
the machines from there. 

# Converting the Master to an AP
After this, the Pi will no longer be connected to the Router and you will only be able 
to access it directly or via the Bridge.

Do **ALL** of these steps in one login session!
~~~
pi@mesh-master:~ $ sudo bash
~~~
If you haven't just run them, run apt-get update and upgrade as usual

## HostApd
Hostapd is an AccessPoint daemon, which provides the WiFi network to all the 
Mesh nodes.
~~~
root@mesh-master:/home/pi# apt-get -y install hostapd
root@mesh-master:/home/pi# systemctl stop hostapd
~~~
Check/Modify System/etc/hostapd/hostapd.conf and copy to /etc

Edit /etc/default/hostapd
Modify (uncomment and fix)
~~~
DAEMON_CONF="/etc/hostapd/hostapd.conf"
~~~

## DnsMasq
DnsMasq is a DHCP and local DNS system. It manages the IP addresses and names etc.
~~~
root@mesh-master:/home/pi# apt-get -y install dnsmasq
root@mesh-master:/home/pi# systemctl stop dnsmasq
~~~
Modify System/etc/add_to_dhcpcd.conf to match your selected IP addresses and then 
add the contents to the end of /etc/dhcpcd.conf.
This sets up the 'wlan0' interface with a static IP address - confirm the details before 
saving.

Create a new /etc/dnsmasq.conf
~~~
root@mesh-master:/home/pi# mv /etc/dnsmasq.conf /etc/dnsmasq.conf.orig
~~~
Check/modify System/etc/dnsmasq.conf and copy it to /etc
Check/modify System/etc/dnsmasq_static_hosts.conf file and copy it to /etc

Enable startup
~~~
root@mesh-master:/home/pi# systemctl unmask hostapd
root@mesh-master:/home/pi# systemctl enable hostapd
root@mesh-master:/home/pi# systemctl unmask dnsmasq
root@mesh-master:/home/pi# systemctl enable dnsmasq
~~~

Reboot

The Master should now be running, and you can connect to it via WiFi using the MeshAccess 
SSID.

# Configuring a Bridge
I used a Raspi-B+ v4 with WiFi and E/net adapters
The v4 required Buster - not Stretch! I used Buster Lite
I just used a different SD card image ... and set it up as per the Base system instructions above.
The Bridge is configured as a Client, and the wpa_supplicant.conf file will connect 
it to the Master (on the wlan0 interface).
~~~
pi@mesh-bridge:~ $ sudo bash
~~~
If you haven't just run them, run apt-get update and upgrade as usual

The next bit will create a one-way Bridge from the Master (on the wlan0 interface) 
and the external network (on eth0).
Mesh Client(s) will use the Bridge to get out to the internet.
It will not allow transparent, two-way access to the individual Nodes, and they will 
not be visible on the external network. 
## IPTables
~~~
root@mesh-bridge:/home/pi# iptables -F
root@mesh-bridge:/home/pi# iptables -t nat -F
root@mesh-bridge:/home/pi# iptables -t mangle -F

root@mesh-bridge:/home/pi# iptables -t nat -A  POSTROUTING -o eth0 -j MASQUERADE
root@mesh-bridge:/home/pi# iptables -A FORWARD -i eth0 -o wlan0 -m state --state RELATED,ESTABLISHED -j ACCEPT
root@mesh-bridge:/home/pi# iptables -A FORWARD -i wlan0 -o eth0 -j ACCEPT
~~~
Edit/modify /etc/sysctl.conf and uncomment the line (remove the #):
~~~
#net.ipv4.ip_forward=1
~~~

Save the tables and enable them to be reloaded on startup
~~~
root@mesh-bridge:/home/pi# mkdir -p /etc/iptables; iptables-save > /etc/iptables/rules.v4
~~~

Edit/Modify the file /etc/rc.local and add the following (BEFORE THE exit 0)
~~~
iptables-restore /etc/iptables/rules.v4
~~~

Reboot

## Extra configuration for a Client
~~~
pi@mesh-xxx:~ $ sudo bash
~~~
### I2C
If you will be using I2C (I do)
~~~
root@mesh-xxx:/home/pi# apt-get -y install -y i2c-tools
~~~
### Wiringpi
Needed for Pi4J (and other low-level GPIO stuff?)
~~~
root@mesh-xxx:/home/pi# apt-get -y install wiringpi
~~~
### Java 8
My preferred development language
~~~
root@mesh-xxx:/home/pi# apt-get -y install openjdk-8-jre
~~~




