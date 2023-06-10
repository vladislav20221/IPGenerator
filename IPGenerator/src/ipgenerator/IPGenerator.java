package ipgenerator;

import java.awt.Point;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;
/**
 *
 * @author Vladislav
 */
public class IPGenerator {

    public static void main ( String[] args ) {
        SwingUtilities.invokeLater( ()-> new mainWindow() );        
    }
}

class IPv4address {    
    int ip;                     // Заданный IPv4 адрес
    // хранится в виде числа битов отданных под маску
    byte mask;                  // Маска сети
    int hostBit;                // Биты в узловой части
    int netBit;                 // Биты в свободной сетевой части
    int netN;                   // Чесло подситей
    int hostN;                  // Число хостов
    
    int maxCount;               // Максимальное число узлов / подсетей
    int maxnetN;                // Максимальное число подситей
    int maxhostN;               // Максимальное число узлов
    
    public void setIPaddress ( String address ) {
        this.mask = Byte.valueOf( address.split(" / ")[1] );
        this.ip = parseAdressIPv4( address );
    }
    
    public IPv4address ( String address ) {
        this.mask = Byte.valueOf( address.split(" / ")[1] );
        this.ip = parseAdressIPv4( address );
    }
          
    public void splitNetworkNumber ( int N ) {        
        this.maxCount = (int)Math.pow( 2, 31-mask );
        netBit = (int)Math.ceil(Math.log(N)/Math.log(2) );      // Биты оставшиеся на адреса подсетей.
        if ( this.netBit+this.mask >= 31 )
            netBit = 30-this.mask;
        hostBit = 32-(mask+netBit);                               // Биты оставленные на адреса хостов.
        getInfo();
    }
    public void splitHostsNumber ( int N ) {
        this.maxCount = (int)Math.pow( 2, 32-mask )-2;
        hostBit = (int)Math.ceil(Math.log(N+2)/Math.log(2) );   // Биты оставшиеся на адреса подсетей.
        if ( 32-this.mask <= hostBit )
            hostBit = 32-this.mask;
        netBit = 32-(mask+hostBit);                               // Биты оставленные на подсети
        getInfo();
    }
    
    private void getInfo (  ) {
        int netAd = getNetworkAddress();                     // Адресс сети.        
        this.netN = (int)Math.pow( 2, netBit );              // Количество подсетей.
        this.hostN = (int)Math.pow( 2, hostBit )-2;          // Количество хостов.                
        
        mainWindow.label_mask_number.setText( (mask+netBit)+"" );
        mainWindow.label_mask.setText( toString( (int)Math.pow( 2, (32-((mask+netBit))) )*-1 ) );
        mainWindow.label_mask_bin.setText( showOctet ( (int)Math.pow( 2, (32-((mask+netBit))) )*-1 ) );
        
        mainWindow.label_host_bit_count.setText( hostBit+"" );
        mainWindow.label_network_bit_count.setText( netBit+"" );
        
        mainWindow.label_network_count.setText( this.netN+"" );
        mainWindow.label_host_count.setText( this.hostN+"" );
        
        SwingUtilities.invokeLater( ()->{
            mainWindow.progresbar.setMaximum( this.netN );
            mainWindow.label_mask.repaint();
        } );        
                
        for ( int i = 0; i<this.netN; i++ ) {
            if ( i%4 == 0 ) {
                final int value = i;
                SwingUtilities.invokeLater( ()-> {
                    JViewport view = mainWindow.scrol.getViewport();
                    view.setViewPosition( new Point ( 0, view.getViewSize().height ) );                        
                    mainWindow.tableModel.update();     
                    mainWindow.progresbar.setValue( value );
                } );   
            }
            mainWindow.tableModel.addRow( new String []{ toString( netAd ),
                                                         toString( netAd+1 )+"-"+toString( netAd+pow2(hostBit)-1 ),
                                                         toString( netAd+pow2(hostBit) ), showOctet( (netAd|(int)Math.pow(2, hostBit)) ) });
            netAd += (int)Math.pow( 2, hostBit );
        }
    }        
    
    public int getIpOc_1 () {return (ip>>8*3)&0xFF;}
    public static int getIpOc_1 ( final int address ) { return (address>>8*3)&0xFF; }
    public int getIpOc_2 () {return (ip>>8*2)&0xFF;}
    public static int getIpOc_2 ( final int address ) { return (address>>8*2)&0xFF; }
    public int getIpOc_3 () {return (ip>>8)&0xFF;}
    public static int getIpOc_3 ( final int address ) { return (address>>8)&0xFF; }
    public int getIpOc_4 () {return ip&0xFF;}
    public static int getIpOc_4 ( final int address ) { return address&0xFF; }
    /**
     * Отображает каждый октет в двоичной форме
     * @param address
     * @return 
     */
    public static String showOctet ( final int address ) {
        int oct_1 = getIpOc_1 ( address );
        int oct_2 = getIpOc_2 ( address );
        int oct_3 = getIpOc_3 ( address );
        int oct_4 = getIpOc_4 ( address );
        String text_oct_1 = String.format( "%8s", Integer.toBinaryString( oct_1 ) ).replaceAll( " ", "0" );
        String text_oct_2 = String.format( "%8s", Integer.toBinaryString( oct_2 ) ).replaceAll( " ", "0" );
        String text_oct_3 = String.format( "%8s", Integer.toBinaryString( oct_3 ) ).replaceAll( " ", "0" );
        String text_oct_4 = String.format( "%8s", Integer.toBinaryString( oct_4 ) ).replaceAll( " ", "0" );
        return text_oct_1+"-"+text_oct_2+"-"+text_oct_3+"-"+text_oct_4;
    }
    /**
     * Отображает каждый октет в двоичной форме
     * @param address
     * @return 
     */
    public static String showOctet ( final String addressstr ) {
        int address = parseAdressIPv4 ( addressstr );
        int oct_1 = getIpOc_1 ( address );
        int oct_2 = getIpOc_2 ( address );
        int oct_3 = getIpOc_3 ( address );
        int oct_4 = getIpOc_4 ( address );
        String text_oct_1 = String.format( "%8s", Integer.toBinaryString( oct_1 ) ).replaceAll( " ", "0" );
        String text_oct_2 = String.format( "%8s", Integer.toBinaryString( oct_2 ) ).replaceAll( " ", "0" );
        String text_oct_3 = String.format( "%8s", Integer.toBinaryString( oct_3 ) ).replaceAll( " ", "0" );
        String text_oct_4 = String.format( "%8s", Integer.toBinaryString( oct_4 ) ).replaceAll( " ", "0" );
        return text_oct_1+"-"+text_oct_2+"-"+text_oct_3+"-"+text_oct_4;
    }
    /**
     * Отображает каждый октет в десятичной форме
     * @param address
     * @return 
     */
    public static String toString ( int address ) {
        return getIpOc_1(address)+"."+getIpOc_2(address)+"."+getIpOc_3(address)+"."+getIpOc_4(address);
    }
    public static int parseAdressIPv4 ( String address ) {
        String[] ipvalue = address.replaceAll( " / \\d{1,2}", "" ).split("\\.");
        int pip = 0;
        pip = (pip+Integer.valueOf(ipvalue[0]))<<8;
        pip = (pip+Integer.valueOf(ipvalue[1]))<<8;
        pip = (pip+Integer.valueOf(ipvalue[2]))<<8;
        pip = (pip+Integer.valueOf(ipvalue[3]));
        return pip;
    }
    /**
     * Заполняет битами слева направо.
     * @param n
     * @return 
     */
    private static int pow2 ( int n ) {
        int pow = 0;
        for ( int i = 0; i<n; i++ )
            pow += (int)Math.pow( 2, i );
        return pow;
    }
    /**
     * Получает адрес сети.
     * @return 
     */
    public int getNetworkAddress () { return ((ip>>(32-mask))&pow2(32-hostBit))<<(32-mask); }    
}