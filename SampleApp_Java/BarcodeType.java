package JposTest.src;

import jpos.ScannerConst;


/**
 * This class is used to get the bar code Type Name when the Integer type code
 * is known.
 *
 */
public class BarcodeType {

    /**
     * Returns the bar code type name that matches with the integer code value
     * @param code : Integer constant 
     * @return Bar code Type Name
     */
    public static String getBarcodeTypeName(int code) {
        String val = "Unknown";
        switch (code) {
            case ScannerConst.SCAN_SDT_UPCA:
                val = "UPC-A";
                break;
            case ScannerConst.SCAN_SDT_UPCE:
                val = " UPC-E";
                break;
            case ScannerConst.SCAN_SDT_JAN8:
                val = "JAN 8 / EAN 8";
                break;
            case ScannerConst.SCAN_SDT_JAN13:
                val = "JAN 13 / EAN 13";
                break;
            case ScannerConst.SCAN_SDT_TF:
                val = "2 of 5";
                break;
            case ScannerConst.SCAN_SDT_ITF:
                val = "Interleaved 2 of 5";
                break;
            case ScannerConst.SCAN_SDT_Codabar:
                val = "Codabar";
                break;
            case ScannerConst.SCAN_SDT_Code39:
                val = "Code 39";
                break;
            case ScannerConst.SCAN_SDT_Code93:
                val = "Code 93";
                break;
            case ScannerConst.SCAN_SDT_Code128:
                val = "Code 128";
                break;
            case ScannerConst.SCAN_SDT_UPCA_S:
                val = " UPC-A with Supplemental";
                break;
            case ScannerConst.SCAN_SDT_UPCE_S:
                val = "UPC-E with Supplemental";
                break;
            case ScannerConst.SCAN_SDT_UPCD1:
                val = "UPC-D1";
                break;
            case ScannerConst.SCAN_SDT_UPCD2:
                val = "UPC-D2";
                break;
            case ScannerConst.SCAN_SDT_UPCD3:
                val = "UPC-D3";
                break;
            case ScannerConst.SCAN_SDT_UPCD4:
                val = "UPC-D4";
                break;
            case ScannerConst.SCAN_SDT_UPCD5:
                val = "UPC-D5";
                break;
            case ScannerConst.SCAN_SDT_EAN8_S:
                val = "EAN-8 with Supplemental";
                break;
            case ScannerConst.SCAN_SDT_EAN13_S:
                val = "EAN-13 with Supplemental";
                break;
            case ScannerConst.SCAN_SDT_EAN128:
                val = "EAN-128";
                break;
            case ScannerConst.SCAN_SDT_OCRA:
                val = "OCR \"A\"";
                break;
            case ScannerConst.SCAN_SDT_OCRB:
                val = "OCR \"B\"";
                break;
            case ScannerConst.SCAN_SDT_PDF417:
                val = "PDF 417";
                break;
            case ScannerConst.SCAN_SDT_MAXICODE:
                val = "MAXICODE";
                break;
            case ScannerConst.SCAN_SDT_RSS_EXPANDED:
                val = "GS1 Databar Expanded";
                break;
            case ScannerConst.SCAN_SDT_AZTEC:
                val = "AZTEC";
                break;
            case ScannerConst.SCAN_SDT_DATAMATRIX:
                val = "Data Matrix";
                break;

            case ScannerConst.SCAN_SDT_QRCODE:
                val = "QR Code";
                break;

            case ScannerConst.SCAN_SDT_UQRCODE:
                val = "Micro QR Code";
                break;

            case ScannerConst.SCAN_SDT_UPDF417:
                val = "Micro PDF417";
                break;

            case ScannerConst.SCAN_SDT_TFMAT:
                val = "Matrix 2 of 5";
                break;

            case ScannerConst.SCAN_SDT_UsPlanet:
                val = "US Planet";
                break;

            case ScannerConst.SCAN_SDT_TRIOPTIC39:
                val = "Trioptic Code 39";
                break;
                
            case ScannerConst.SCAN_SDT_ISBT128:
                val = "ISBT 128";
                break;
                
            case ScannerConst.SCAN_SDT_Code11:
                val = "Code 11";
                break;
                
            case ScannerConst.SCAN_SDT_MSI:
                val = "MSI";
                break;

            case ScannerConst.SCAN_SDT_GS1DATAMATRIX:
                val = "GS1 DataMatrix";
                break;

            case ScannerConst.SCAN_SDT_GS1QRCODE:
                val = "GS1 QR Code";
                break;

            case ScannerConst.SCAN_SDT_DutchKix:
                val = "Dutch Postal";
                break;

            case ScannerConst.SCAN_SDT_JapanPost:
                val = "Japan Postal";
                break;

            case ScannerConst.SCAN_SDT_AusPost:
                val = "Australian Postal";
                break;

            case ScannerConst.SCAN_SDT_UkPost:
                val = "UK Postal";
                break;

            case ScannerConst.SCAN_SDT_PLESSEY:
                val = "Plessey Code";
                break;

            case ScannerConst.SCAN_SDT_GS1DATABAR:
                val = "GS1 Databar";
                break;
                
            case ScannerConst.SCAN_SDT_PostNet:
                val="US Postnet";
                break;

	    case ScannerConst.SCAN_SDT_Code49:
                val="Code 49";
                break;             

            case ScannerConst.SCAN_SDT_UNKNOWN:
                val = "Unknown";
                break;

            case ScannerConst.SCAN_SDT_OTHER:
                val = "Other";
                break;
        }
        return val;
    }

}
