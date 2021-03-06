package org.labkey.wnprc_billing.invoice;

import com.koadweb.javafpdf.Alignment;
import com.koadweb.javafpdf.Coordinate;
import com.koadweb.javafpdf.DrawMode;
import com.koadweb.javafpdf.FPDF;
import com.koadweb.javafpdf.FontStyle;
import com.koadweb.javafpdf.Format;
import com.koadweb.javafpdf.ImageType;
import com.koadweb.javafpdf.Position;
import org.apache.commons.lang3.StringUtils;
import org.labkey.api.util.FileUtil;
import org.labkey.api.util.UnexpectedException;
import org.labkey.wnprc_billing.domain.Alias;
import org.labkey.wnprc_billing.domain.Invoice;
import org.labkey.wnprc_billing.domain.InvoiceRun;
import org.labkey.wnprc_billing.domain.InvoicedItem;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static java.lang.Math.sqrt;

public class InvoicePDF extends FPDF
{
    private String payment_info = "";
    private double tier_rate;
    private Invoice invoice;
    private Alias alias;
    private InvoiceRun invoiceRun;
    private double grandTotal = 0.0;

    SimpleDateFormat dateFormat = new SimpleDateFormat("M/d/yy");
    SimpleDateFormat dateFormatBillingFor = new SimpleDateFormat("MM-dd-yyyy");
    DecimalFormat moneyFormat = new DecimalFormat("#,##0.00");
    DecimalFormat quantityFormat = new DecimalFormat("#,##0.00");

    String companyName = "Wisconsin National Primate Research Center";
    String companyAddress = "University of Wisconsin - Madison\n";
    String type = "Totally fake";
    int page_number = 1;
    String footer_text = "For questions regarding this invoice contact ";
    private String _creditToAccount;
    private String _chargeLine;

    int angle = 0;

    public InvoicePDF(Invoice invoice, Alias alias, InvoiceRun invoiceRun, double tierRate, String contactEmail, String billingAddress, String creditToAccount, String chargeLine)
    {
        super(Format.LETTER);
        this.alias = alias;
        this.invoiceRun = invoiceRun;
        this.invoice = invoice;
        this.tier_rate = tierRate;
        companyAddress += billingAddress;
        _creditToAccount = creditToAccount;
        footer_text += contactEmail;
        _chargeLine = chargeLine;
    }


    public void createLineItems(List<InvoicedItem> invoicedItems, boolean includeSubtotal) throws IOException
    {
        List<FormattedLineItem> items = new ArrayList<>();
        Calendar calendarCurrent = Calendar.getInstance();
        Calendar calendarItem = Calendar.getInstance();
        boolean isFirstItem = true;
        String currentGroup = null;
        float subTotal = 0;

        for (InvoicedItem invoicedItem : invoicedItems)
        {
            String groupHeader = (null == invoicedItem.getCategory() ? invoicedItem.getGroupName() : invoicedItem.getCategory());

            calendarItem.setTime(invoicedItem.getDate());
            boolean isDateChange = isFirstItem || calendarCurrent.get(Calendar.DAY_OF_MONTH) != calendarItem.get(Calendar.DAY_OF_MONTH);
            boolean isGroupChange = false;
            if(invoicedItem.getGroupName()== null){
                isGroupChange = currentGroup != null;
            }
            else {
                isGroupChange = !invoicedItem.getGroupName().equals(currentGroup);
            }

            if(includeSubtotal) {
                if((isDateChange || isGroupChange) && !isFirstItem ){
                    items.add(new FormattedLineItem(null,"Sub total:", null,   null, subTotal, true));
                    subTotal = 0;
                }
            }

            if (isDateChange){
                items.add(new FormattedLineItem(invoicedItem.getDate(), groupHeader, null, null, null, true));
                currentGroup = invoicedItem.getGroupName();
                calendarCurrent.setTime(invoicedItem.getDate());
                isFirstItem = false;
            }

            if (!isDateChange && isGroupChange){
                items.add(new FormattedLineItem(null, groupHeader, null, null, null, true));
                currentGroup = invoicedItem.getGroupName();
            }

            subTotal += invoicedItem.getTotalCost();
            grandTotal += invoicedItem.getTotalCost();

            items.addAll(getLineItemsFromInvoicedItem(invoicedItem));
        }

        if(includeSubtotal) {
            items.add(new FormattedLineItem(null, "Sub total:", null, null, subTotal, true));
        }
        addLines(items);
    }

    protected List<FormattedLineItem> getLineItemsFromInvoicedItem(InvoicedItem invoicedItem){
        String indent = "  ";
        List<FormattedLineItem> formattedLineItems = new ArrayList<>();
        boolean showDetailsWithItem = invoicedItem.getComment() == null;
        String participantId = invoicedItem.getId() == null? "": " - " + invoicedItem.getId();

        List<FormattedLineItem> itemLines = new ArrayList<>();
        List<FormattedLineItem> commentLines = new ArrayList<>();

        if (invoicedItem.getItem() != null || showDetailsWithItem) {
            if (invoicedItem.getItem() != null && invoicedItem.getItem().length() > 60) {
                FormattedLineItem itemLine = new FormattedLineItem();
                itemLine._description = indent + invoicedItem.getItem().substring(0, 60);
                itemLines.add(itemLine);
                for (int i = 60; i < invoicedItem.getItem().length(); i+= 60) {
                    itemLine = new FormattedLineItem();
                    if (i + 60 >= invoicedItem.getItem().length()) {
                        itemLine._description = indent + invoicedItem.getItem().substring(i) + participantId;
                    } else {
                        itemLine._description = indent + invoicedItem.getItem().substring(i, i + 60);
                    }
                    itemLines.add(itemLine);
                }
            } else {
                FormattedLineItem itemLine = new FormattedLineItem();
                itemLine._description = indent + invoicedItem.getItem() + participantId;
                participantId = "";//don't duplicate on the comment line
                itemLines.add(itemLine);
            }
            indent += "  ";
        }

        if (invoicedItem.getComment() != null) {
            if (invoicedItem.getComment().length() > 60) {
                //break the comment up into multiple lines of 60 characters (or less) each
                //and indent any line after the first
                //TODO consider making line breaks at word boundaries instead of 60 characters
                FormattedLineItem commentLine = new FormattedLineItem();
                commentLine._description = indent + invoicedItem.getComment().substring(0, 60);
                commentLines.add(commentLine);
                for (int i = 60; i < invoicedItem.getComment().length(); i+= 58) {
                    commentLine = new FormattedLineItem();
                    //only add participantId to the last line of the comment
                    if (i + 58 >= invoicedItem.getComment().length()) {
                        commentLine._description = indent + indent + invoicedItem.getComment().substring(i) + participantId;
                    } else {
                        commentLine._description = indent + indent + invoicedItem.getComment().substring(i, i + 58);
                    }
                    commentLines.add(commentLine);
                }
            } else {
                FormattedLineItem commentLine = new FormattedLineItem();
                commentLine._description = indent + invoicedItem.getComment() + participantId;
                commentLines.add(commentLine);
            }
        }


        if (showDetailsWithItem) {
            //only add details to the first line of the item so they're not duplicated for each line
            addDetailsToLineItem(itemLines.get(0), invoicedItem);
        } else {
            //only add details to the first line of the comment so they're not duplicated for each line
            addDetailsToLineItem(commentLines.get(0), invoicedItem);
        }

        if (itemLines.size() > 0) {
            for (FormattedLineItem itemLine : itemLines) {
                formattedLineItems.add(itemLine);
            }
        }

        if (commentLines.size() > 0) {
            for (FormattedLineItem commentLine : commentLines) {
                formattedLineItems.add(commentLine);
            }
        }
        return formattedLineItems;
    }

    private void addDetailsToLineItem(FormattedLineItem formattedLineItem, InvoicedItem invoicedItem ){
        formattedLineItem._quantity = invoicedItem.getQuantity();
        if(invoicedItem.getUnitCost() != null)
            formattedLineItem._unitPrice = invoicedItem.getUnitCost().floatValue();
        formattedLineItem._linePrice = invoicedItem.getTotalCost().floatValue();
    }

    @Override
    public void Footer()
    {
        try
        {
            addTotals();
            addFooterText();
        }
        catch (IOException e)
        {
            throw new UnexpectedException(e);
        }
    }

    // total width is 190
    protected static abstract class Column
    {
        private final String _header;
        private final int _width;
        private final Alignment _align;

        public Column(String header, int width, Alignment align)
        {
            _header = header;
            _width = width;
            _align = align;
        }

        public int getWidth()
        {
            return _width;
        }

        public String getHeader()
        {
            return _header;
        }

        public Alignment getAlign()
        {
            return _align;
        }

        public abstract String getValue(FormattedLineItem lineItem);
    }

    List<Column> headers = Arrays.asList(
            new Column("Charge Date", 25, Alignment.CENTER)
            {
                @Override
                public String getValue(FormattedLineItem lineItem)
                {
                    return lineItem._chargeDate == null? "--" : dateFormat.format(lineItem._chargeDate);
                }
            },
            new Column("Description", 111, Alignment.LEFT)
            {
                @Override
                public String getValue(FormattedLineItem lineItem)
                {
                    return lineItem._description == null?"": lineItem._description;
                }
            },
            new Column("Quantity", 16, Alignment.RIGHT)
            {
                @Override
                public String getValue(FormattedLineItem lineItem)
                {
                    return lineItem._quantity == null? "": quantityFormat.format(lineItem._quantity);
                }
            },
            new Column("Unit Price", 20, Alignment.RIGHT)
            {
                @Override
                public String getValue(FormattedLineItem lineItem)
                {
                    return lineItem._unitPrice == null? "": moneyFormat.format(lineItem._unitPrice);
                }
            },
            new Column("Line Price", 22, Alignment.RIGHT)
            {
                @Override
                public String getValue(FormattedLineItem lineItem)
                {

                    return lineItem._linePrice == null ?"": moneyFormat.format(lineItem._linePrice);
                }
            });

    public List<Column> getHeaders()
    {
        return headers;
    }

    public static class FormattedLineItem
    {
        private String _description;
        private Date _chargeDate;
        private Double _quantity;
        private Float _unitPrice;
        private boolean _isBold;
        public Float _linePrice;

        public String get_description() {
            return _description;
        }

        public Date get_chargeDate() {
            return _chargeDate;
        }

        public Double get_quantity() {
            return _quantity;
        }

        public Float get_unitPrice() {
            return _unitPrice;
        }

        public boolean is_isBold() {
            return _isBold;
        }

        public Float get_linePrice() {
            return _linePrice;
        }

        public FormattedLineItem(Date chargeDate, String description, Double quantity, Float unitPrice, Float linePrice, boolean isBold)
        {
            _chargeDate = chargeDate;
            _description = description;
            _quantity = quantity;
            _unitPrice = unitPrice;
            _linePrice = linePrice;
            _isBold = isBold;
        }

        public FormattedLineItem()
        {
            _chargeDate = null;
            _description = null;
            _quantity = null;
            _unitPrice = null;
        }
    }


    @Override
    public void Header()
    {
        try
        {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            try (InputStream in = getClass().getResourceAsStream("/images/UWMadisonSeal.PNG"))
            {
                FileUtil.copyData(in, out);
            }
            Image("UWMadisonSeal.PNG", out.toByteArray(), new Coordinate(46, 4), 14, 15, ImageType.PNG, 0, false);

            addCompany(companyName);
            addAddress(companyAddress);
            addDate(invoiceRun.getRunDate());
            addGrant();
            addPageNumber(page_number);
            page_number++;
            addGrantAddress();
            addInvoiceNo();
            addChargeLine();
            addCreditLine();
            addPaymentInfo();
            addAccountContact(StringUtils.isNotBlank(alias.getBilling_contact_info()) ? alias.getBilling_contact_info() : "Billing Contact Email Not Specified");
            addBillingDate(invoiceRun.getBillingPeriodStart(), invoiceRun.getBillingPeriodEnd());
            addCols(this.getHeaders());

            setY(75);
        }
        catch (IOException e)
        {
            throw new UnexpectedException(e);
        }
    }

    // private functions
    private void RoundedRect(double x, double y, double w, double h, double r, String style)
    {
        float k = this.k;
        double hp = this.h;
        String op;
        switch (style)
        {
            case "F":
                op = "f";
                break;
            case "FD":
            case "DF":
                op = "B";
                break;
            default:
                op = "S";
        }

        double MyArc = 4 / 3 * (sqrt(2) - 1);
        _out(String.format("%.2f %.2f m", (x + r) * k, (hp - y) * k));
        double xc = x + w - r;
        double yc = y + r;
        _out(String.format("%.2f %.2f l", xc * k, (hp - y) * k));
        _Arc(xc + r * MyArc, yc - r, xc + r, yc - r * MyArc, xc + r, yc);
        xc = x + w - r;
        yc = y + h - r;
        _out(String.format("%.2f %.2f l", (x + w) * k, (hp - yc) * k));
        _Arc(xc + r, yc + r * MyArc, xc + r * MyArc, yc + r, xc, yc + r);
        xc = x + r;
        yc = y + h - r;
        _out(String.format("%.2f %.2f l", xc * k, (hp - (y + h)) * k));
        _Arc(xc - r * MyArc, yc + r, xc - r, yc + r * MyArc, xc - r, yc);
        xc = x + r;
        yc = y + r;
        _out(String.format("%.2f %.2f l", (x) * k, (hp - yc) * k));
        _Arc(xc - r, yc - r * MyArc, xc - r * MyArc, yc - r, xc, yc - r);
        _out(op);
    }

    private void _Arc(double x1, double y1, double x2, double y2, double x3, double y3)
    {
        float h = this.h;
        _out(String.format("%.2f %.2f %.2f %.2f %.2f %.2f c ", x1 * k, (h - y1) * k, x2 * k, (h - y2) * k, x3 * k, (h - y3) * k));
    }

    protected void _endpage()
    {
        if (angle != 0)
        {
            angle = 0;
            _out("Q");
        }
        super._endpage();
    }

    // public functions
    private int sizeOfText(String text, float width)
    {
        int index = 0;
        int nb_lines = 0;
        boolean loop = true;
        String line;
        while (loop)
        {
            int pos = text.indexOf("\n");
            if (pos < 0)
            {
                loop = false;
                line = text;
            }
            else
            {
                line = text.substring(index, pos);
                text = text.substring(pos + 1);
            }
            int length = (int) Math.floor(getStringWidth(line));
            width = (width == 0) ? 1 : width;
            double res = 1 + Math.floor(length / width);
            nb_lines += res;
        }
        return nb_lines;
    }

    // Company
    private void addCompany(String name) throws IOException
    {
        float x1 = 20;
        float y1 = 5;
        setXY(x1, y1);
        setFont("Arial", Collections.singleton(FontStyle.BOLD), 13);
        int length = 0;//GetStringWidth (name);
        setTextColor(199, 0, 49);
        Cell(length, 2, name, Position.BELOW, Alignment.CENTER);
    }

    private void addAddress(String address) throws IOException
    {
        int x1 = 20;
        int y1 = 5;

        setXY(x1, y1 + 3);
        setFont("Arial", Collections.emptySet(), 10);
        setTextColor(1, 1, 1);
        float length = 0;//GetStringWidth (adress);
        MultiCell(length, 4, address, null, Alignment.CENTER, false);
    }

    // Client address
    private void addGrantAddress() throws IOException
    {
        float left_x = 10;
        float right_x = 30;

        float x = left_x;
        float y = 20;
        // to is used for all accounts
        setXY(x, y);
        setFont("Arial", Collections.singleton(FontStyle.BOLD), 10);
        MultiCell(20, 3, "To:", null, Alignment.RIGHT, false);
        x = right_x;
        setXY(x, y);
        setFont("Arial", Collections.emptySet(), 10);
        String address = formatAddess(alias);
        MultiCell(76, 3, address);

        String req_text;

        // type is blankets only
        if ("internal_uw".equals(type))
        {
            req_text = "PO. No.:";

            x = left_x;
            y = getY() + 2;
            setXY(x, y);
            setFont("Arial", Collections.singleton(FontStyle.BOLD), 10);
            //MultiCell( 20, 4, 'Type:', 0, 'R');
            x = right_x;
            setXY(x, y);
            setFont("Arial", Collections.emptySet(), 10);
            //MultiCell( 60, 4, "partial");
        }
        else
        {
            req_text = "PO No.:";
        }
        x = left_x;
        y = getY() + 2;
        setXY(x, y);
        setFont("Arial", Collections.singleton(FontStyle.BOLD), 10);
        MultiCell(20, 5, req_text, null, Alignment.RIGHT, false);
        x = right_x;
        setXY(x, y);
        setFont("Arial", Collections.emptySet(), 10);
        MultiCell(60, 5, alias.getPo_number() == null? "": alias.getPo_number());
        x = left_x;
        y = getY();
        setXY(x, y);
        setFont("Arial", Collections.singleton(FontStyle.BOLD), 10);
        MultiCell(20, 5, "Expires:", null, Alignment.RIGHT, false);
        x = right_x;
        setXY(x, y);
        setFont("Arial", Collections.emptySet(), 10);

        MultiCell(60, 4, dateFormat.format(alias.getBudgetEndDate()));
    }

    private void addDate(Date date) throws IOException
    {
        float r1 = w - 66;
        float r2 = r1 + 25;
        float y1 = 19;
        float y2 = y1 - 2;
        float mid = y1 + (y2 / 2);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        String formattedDate = dateFormat.format(date);
        RoundedRect(r1, y1, (r2 - r1), y2, 3.5f, "D");
        Line(new Coordinate(r1, mid), new Coordinate(r2, mid));
        setXY(r1 + (r2 - r1) / 2 - 5, y1 + 3);
        setFont("Helvetica", Collections.singleton(FontStyle.BOLD), 10);
        Cell(10, 2, "DATE", Alignment.CENTER);
        setXY(r1 + (r2 - r1) / 2 - 5, y1 + 11);
        setFont("Helvetica", Collections.emptySet(), 10);
        Cell(10f, 2f, formattedDate, Alignment.CENTER);
    }

    private void addGrant() throws IOException
    {
        float r1 = w - 40;
        float r2 = r1 + 31;
        float y1 = 19;
        float y2 = y1 - 2;
        float mid = y1 + (y2 / 2);
        RoundedRect((int) r1, (int) y1, (int) (r2 - r1), (int) y2, 3.5, "D");
        Line(new Coordinate(r1, mid), new Coordinate(r2, mid));
        setXY(r1 + (r2 - r1) / 2 - 5, y1 + 3);
        setFont("Helvetica", Collections.singleton(FontStyle.BOLD), 10);
        Cell(10, 2, "FUND-ACCOUNT", Alignment.CENTER);
        setXY(r1 + (r2 - r1) / 2 - 5, y1 + 11);
        setFont("Helvetica", Collections.emptySet(), 10);
        Cell(10, 2,   addItem(alias.getUw_fund()) + alias.getGrantNumber(), Alignment.CENTER);
    }

    private void addPageNumber(int page) throws IOException
    {
        float r1 = w - 83;
        float r2 = r1 + 17;
        float y1 = 19;
        float y2 = y1 - 2;
        float mid = y1 + (y2 / 2);
        RoundedRect(r1, y1, (r2 - r1), y2, 3.5, "D");
        Line(new Coordinate(r1, mid), new Coordinate(r2, mid));
        setXY(r1 + (r2 - r1) / 2 - 5, y1 + 3);
        setFont("Helvetica", Collections.singleton(FontStyle.BOLD), 10);
        Cell(10, 2, "PAGE", Alignment.CENTER);
        setXY(r1 + (r2 - r1) / 2 - 5, y1 + 11);
        setFont("Helvetica", Collections.emptySet(), 10);
        Cell(10, 2, Integer.toString(page), Alignment.CENTER);
    }

    // billing date
    private void addBillingDate(Date billing_period_start_date, Date billing_period_end_date) throws IOException
    {
        float x = 10;
        float y = 60;

        setXY(x, y);
        setFont("Arial", Collections.singleton(FontStyle.BOLD), 12);

        MultiCell(0, 4, dateFormatBillingFor.format(billing_period_start_date) + " to " + dateFormatBillingFor.format(billing_period_end_date), null, Alignment.CENTER, false);
    }

    // comments
    private void addAccountContact(String contact) throws IOException
    {
        float x = 10;
        float y = 52;

        setXY(x, y);
        setFont("Arial", Collections.emptySet(), 8);
        setFillColor(230, 230, 230);
        MultiCell(w - 20, 3, "\n");
        MultiCell(w - 20, 3, (StringUtils.trimToNull(contact)), null, Alignment.LEFT, false);
    }

    // payment info
    private void addInvoiceNo() throws IOException
    {
        float x = w - 98;
        float y = 40;

        setXY(x, y);
        String font = "Courier";//: "Arial";
        setFont(font, Collections.singleton(FontStyle.BOLD), 11);
        String invoiceNo = "INVOICE NO. ";
        invoiceNo += invoice.getInvoiceNumber();
        if (!alias.getType().toLowerCase().contains("internal"))
        {
            invoiceNo += "\nMake check payable to: Wisconsin National Primate Research Center";
        }
        MultiCell(0, 4, invoiceNo, null, Alignment.LEFT, false);
    }

    private void addChargeLine() throws IOException
    {
        float x = w - 98;
        float y = 45;

        setXY(x, y);
        String font = "Courier";
        setFont(font, Collections.singleton(FontStyle.BOLD), 11);
        String chargeLine = (StringUtils.isNotBlank(_chargeLine)) ? ("CHARGE: " + _chargeLine) : "";
        MultiCell(0, 4, chargeLine, null, Alignment.LEFT, false);
    }

    private void addCreditLine() throws IOException
    {
        float x = w - 98;
        float y = 50;

        setXY(x, y);
        String font = "Courier";
        setFont(font, Collections.singleton(FontStyle.BOLD), 11);
        String creditLine = (StringUtils.isNotBlank(_creditToAccount)) ? ("CREDIT: " + _creditToAccount) : "";
        MultiCell(0, 4, creditLine, null, Alignment.LEFT, false);

    }

    private String addItem(String item)
    {
        if(item != null){
            return " " + item;
        }
        return "";
    }

    // payment info
    private void addPaymentInfo() throws IOException
    {
        float x = w - 98;
        float y = 45;

        setXY(x, y);
        String font = "internal_uw".equals(type) ? "Courier" : "Arial";
        setFont(font, Collections.singleton(FontStyle.BOLD), 11);
        MultiCell(0, 4, payment_info, null, Alignment.LEFT, false);
    }

    // footer text
    private void addFooterText() throws IOException
    {
        float x = 10;
        float y = h - 40;

        setXY(x, y);
        setFont("Arial", Collections.singleton(FontStyle.BOLD), 10);
        MultiCell(100, 4, footer_text, null, Alignment.LEFT, false);
    }

    private void addTotals() throws IOException
    {
        float r1 = w - 90;
        float r2 = r1 + 80;
        float y1 = h - 40;
        float y2 = y1 + 26;
        RoundedRect(r1, y1, (r2 - r1), (y2 - y1), 2.5, "D");
        Line(new Coordinate(r1 + 50, y1), new Coordinate(r1 + 50, y2));
        setFont("Arial", Collections.singleton(FontStyle.BOLD), 9);
        setXY(r1, y1 + 2);

        Cell(48, 4, "CHARGES THIS MONTH", Alignment.RIGHT);

        setXY(r1, y1 + 8);
        Cell(48, 4, "TIER RATE", Alignment.RIGHT);

        setXY(r1, y1 + 14);
        Cell(48, 4, "OVERHEAD ASSESSMENT", Alignment.RIGHT);
        setXY(r1, y1 + 20);

        Cell(48, 4, "BALANCE DUE", Alignment.RIGHT);
        float r3 = r2 - 25;
        setFont("Arial", Collections.emptySet(), 10);
        setXY(r3, y1 + 2);

        Cell(17, 4, moneyFormat.format(grandTotal), Alignment.RIGHT);
        setXY(r3, y1 + 8);
        Cell(17, 4, moneyFormat.format(tier_rate), Alignment.RIGHT);
        setXY(r3, y1 + 14);
        Cell(17, 4, moneyFormat.format(grandTotal * tier_rate), Alignment.RIGHT); //overhead assessment
        setXY(r3, y1 + 20);

        Cell(17, 4, moneyFormat.format(grandTotal + (grandTotal * tier_rate)), Alignment.RIGHT); //grandtotal plus overhead assessment
    }

    private void addCols(List<Column> headers) throws IOException
    {
        setFont("Helvetica", Collections.singleton(FontStyle.BOLD), 10);
        float r1 = 10;
        float r2 = w - (r1 * 2);
        float y1 = 65;
        float y2 = h - 47 - y1;
        setXY(r1, y1);
        Rect(new Coordinate(r1, y1), r2, y2, DrawMode.CLOSED);
        Line(new Coordinate(r1, y1 + 6), new Coordinate(r1 + r2, y1 + 6));
        float colX = r1;
        int pos = 0;
        Column lastHeader = headers.get(headers.size() - 1);
        for (Column header : headers)
        {
            setXY(colX, y1 + 2);
            Cell(pos, 1, header.getHeader(), Alignment.LEFT);
            colX += header.getWidth();
            if (header != lastHeader)
            {
                Line(new Coordinate(colX, y1), new Coordinate(colX, y1 + y2));
            }
        }
    }

    public void addLines(List<FormattedLineItem> lineItems) throws IOException
    {
        boolean newPage = false;
        for (FormattedLineItem lineItem : lineItems)
        {
            if (newPage) {
                addPage(currentOrientation);
                newPage = false;
            }
            float size = addLine(getY(), lineItem, lineItem._isBold);
            setY(size + 1);
            if (getY() >= 225)
                newPage = true;
        }
    }

    public float addLine (float line, FormattedLineItem lineItem, boolean bold) throws IOException
    {
        int xLocation = 10;
        if(bold){
            line+=2;
        }

        float maxSize      = line;


        if (bold)
            setFont("Helvetica", Collections.singleton(FontStyle.BOLD), 9);
        else
            setFont("Helvetica", Collections.emptySet(), 9);

        for (Column header : this.getHeaders())
        {
            int cellWidth  = header._width -2;
            String s = header.getValue(lineItem);
            setXY( xLocation, line);
            MultiCell( cellWidth, 4 , s, null, header.getAlign(), false);
            if ( maxSize < (getY()  ) )
                maxSize = getY() ;
            xLocation += header._width;
        }
        return line + 2;
    }

    private String formatAddess(Alias alias)
    {
        //check for pi name before appending (only show on internal invoices)
        //check for institution before appending
        //always assume address will be present
        //check for city/state/zip before appending
        StringBuilder address = new StringBuilder();
        if (alias.getType().toLowerCase().contains("internal") && StringUtils.isNotBlank(alias.getInvestigatorName()))
        {
            address.append(alias.getInvestigatorName()).append("\n");
        }
        if (StringUtils.isNotBlank(alias.getInstitution()))
        {
            address.append(alias.getInstitution()).append("\n");
        }
        address.append(alias.getAddress());
        if (StringUtils.isNotBlank(alias.getCity()) && StringUtils.isNotBlank(alias.getState()) && StringUtils.isNotBlank(alias.getZip()))
        {
            address.append("\n").append(alias.getCity()).append(", ").append(alias.getState()).append(" ").append(alias.getZip());
        }

        return address.toString();
    }

}
