package com.itko.lisa.ext.filter;

import java.sql.ResultSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Element;

import com.itko.lisa.ext.util.VseTransactionHelper;
import com.itko.lisa.test.FilterBaseImpl;
import com.itko.lisa.test.TestDefException;
import com.itko.lisa.test.TestExec;
import com.itko.lisa.test.TestRunException;
import com.itko.util.ParameterList;
import com.itko.util.XMLObjectSerialization;

/**
 * Saves a <code>java.sql.ResultSet</code> or XML serialized
 * <code>ResultSet</code> response into a form that the VSE JDBC responder step
 * can use.
 * 
 * @author <a href="mailto:luther@itko.com">Luther Birdzell</a>
 */
public class VseJdbcSaveLastResponseForResponderFilter
        extends FilterBaseImpl
{
    private static final transient Log log              = LogFactory.getLog(VseJdbcSaveLastResponseForResponderFilter.class);
    private static final long          serialVersionUID = -4277340765065607538L;

    /**
     * Get the display name for this filter.
     * 
     * @return a <code>String</code>
     */
    public String getTypeName()
    {
        return "VSE JDBC Save Last Response For Responder Filter";
    }

    /**
     * 
     * 
     * @param testExec
     * @return a <code>boolean</code>
     * @throws TestRunException
     */
    public boolean subPreFilter(TestExec testExec) throws TestRunException
    {
        // nothing to do here
        return false;
    }

    /**
     * Build the list of parameters for this filter.
     * 
     * @return a <code>ParameterList</code>
     */
    public ParameterList getParameters()
    {
        ParameterList p = new ParameterList();

        return p;
    }

    /**
     * Once constructed, we take the XML Element and get our field data from
     * there.
     * 
     * @param e
     * @throws TestDefException
     */
    public void initialize(Element e) throws TestDefException
    {

    }

    /**
     * 
     * 
     * @param testExec
     * @return a <code>boolean</code>
     * @throws TestRunException
     */
    public boolean subPostFilter(TestExec testExec) throws TestRunException
    {
        try
        {
            Object responseObj = testExec.getLastResponse();
            ResultSet rs = null;

            // Load result set xml from file system, etc
            if ( responseObj instanceof String )
            {
                rs = ( ResultSet ) XMLObjectSerialization.xmlToObject(( String ) responseObj);
            }

            // response from a custom step

            // NOTE: the JDBC step returns a
            // com.itko.lisa.jdbc.JDBCResultSetCache object
            // which does not have enough information to build a
            // VSE JDBC response
            else if ( responseObj instanceof ResultSet )
            {
                rs = ( ResultSet ) responseObj;
            }

            String response = VseTransactionHelper.getJdbcResponse( rs );

            testExec.saveNodeResponse(testExec.getCurrentNodeName(), response);

            testExec.setStateValue( VseTransactionHelper.PROPERTY_LISA_VSE_RESPONSE,
                                   response);
        }
        catch ( Exception e )
        {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        }

        return false;
    }
}
