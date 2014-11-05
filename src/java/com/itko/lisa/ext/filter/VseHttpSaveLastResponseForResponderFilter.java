package com.itko.lisa.ext.filter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Element;

import com.itko.lisa.ext.util.VseTransactionHelper;
import com.itko.lisa.gui.XMLResultsCache;
import com.itko.lisa.test.FilterBaseImpl;
import com.itko.lisa.test.TestDefException;
import com.itko.lisa.test.TestExec;
import com.itko.lisa.test.TestRunException;
import com.itko.util.ParameterList;

/**
 * Saves a text response into a form that the VSE HTTP responder step can use.
 * 
 * @author <a href="mailto:luther@itko.com">Luther Birdzell</a>
 */
public class VseHttpSaveLastResponseForResponderFilter
        extends FilterBaseImpl
{
    private static final transient Log log              = LogFactory.getLog(VseHttpSaveLastResponseForResponderFilter.class);
    private static final long          serialVersionUID = -3835598341438285224L;

    /**
     * Get the display name for this filter.
     * 
     * @return a <code>String</code>
     */
    public String getTypeName()
    {
        return "VSE HTTP Save Last Response For Responder Filter";
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
            String responseBody = null;

            // load response from file system, etc
            if ( responseObj instanceof String )
            {
                responseBody = ( String ) testExec.getLastResponse();
            }

            // result format of a raw soap request
            else if ( responseObj instanceof XMLResultsCache )
            {
                XMLResultsCache rsCache = ( XMLResultsCache ) responseObj;
                responseBody = rsCache.getXMLString();
            }

            String response = VseTransactionHelper.getHttpResponse( responseBody );

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
