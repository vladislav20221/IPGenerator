package ipgenerator;

import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

/**
 *
 * @author Vladislav
 */
public class CopyCliipboardAction extends AbstractAction {
    private JLabel label;
    
    public CopyCliipboardAction ( final JLabel label ) {         
        this.label = label; 
        putValue( Action.SHORT_DESCRIPTION, "Скопиролвать в буфер обмена" );
        putValue( Action.SMALL_ICON, new ImageIcon ( "src\\copy.png" ) );        
    }
    
    @Override
    public void actionPerformed( final ActionEvent ae ) {
        Toolkit.getDefaultToolkit()
               .getSystemClipboard()
               .setContents( new StringSelection ( label.getText() ), null );
    }       
}