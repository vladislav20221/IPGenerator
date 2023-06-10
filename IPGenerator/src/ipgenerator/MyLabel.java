package ipgenerator;

import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JLabel;

/**
 *
 * @author Vladislav
 */
public class MyLabel extends JLabel {
    // Отступ
    private static int pdt = 0;
    private static int pdtF = 0;
    
    public static final Color yellow = new Color ( 255, 210, 30 );
    public static final Color green = new Color ( 190, 255, 70 );
    public static final Color grey = new Color ( 200, 200, 200 );
    
    private IPv4address ipaddress;
    
    public MyLabel ( IPv4address ipaddress ) {this.ipaddress = ipaddress;}
    
    @Override
    public void paintComponent ( Graphics g ) {
        pdt = getWidth ( ipaddress.mask ); 
        pdtF = getWidth ( ipaddress.netBit+ipaddress.mask ) - pdt;     
        // Серый
        g.setColor( grey );
        g.fillRect( 0, 0, this.getWidth(), this.getHeight() );
        // Зелёный
        g.setColor( green );
        g.fillRect( pdt, 0, pdtF, this.getHeight() );
        // Жёлтый
        g.setColor( yellow );
        g.fillRect( 0, 0, pdt, this.getHeight() );
        super.paintComponent( g );
    }
    
    private int getWidth ( int n ) {
        if ( n > 24 ) n += 3;
        else if ( n > 16 ) n += 2;
        else if ( n > 8 ) n += 1;        
        char[] ch = "00000000-00000000-00000000-00000000".toCharArray();
        int w = 0;
        for ( int i = 0; i < n; ) {
            if ( ch[i++] == '0' ) w += 7;
            else w += 4;
        }
        return w;
    }
}