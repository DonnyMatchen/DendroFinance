This application is designed for management of personal finance and small business finance data.  It is specifically geared towards trading activity, and that portion differs from GAAP in a few ways.

This project requires Java 17.

<a href="https://github.com/DonnyMatchen/DendroFinance/releases">Download</a>

<b>SINGLE ENTRY, DOUBLE ENTRY, AND QUADRUPLE ENTRY</b><br>
Tracking accounts and Tax accounts are single entry.<br>
Tracking accounts are used to track the volume of all assets that are not the main currency.  This includes other fiat currencies, stocks, cryptocurrencies, commodities, and other forms of inventory.<br>
Tax accounts are ghost accounts, used to track tax-relevent totals such as taxable income, capital gains and losses, and exemptable expenses.<br>
<br>
Normal accounts use double entry accounting, and adhere largely with GAAP.  For more information on standard accounting practices, see any number of excellent guides on double entry accounting.  An understanding of assets, liabilities, and equity--as well as debits anc credits--is necisary to opperate this software.<br><br>
Trading activities use quadruple entry accounting.  This involves the use of an equity account to allow transfers from one asset account to another to be recorded in revenue and expenses.<br>
As an example, if you were buying 1 share of MSFT, this would be represented under double entry as a credit to cash of 200 and debit to stocks of 200.  Under quadruple accounting, this is represented by the same debit and credit, with an aditional debit to Trading_Expenses and credit to an equity account.  This allows for tracking of trading activities in an in depth way.<br>
Another application of quadruple entry accounting is in savings. allowing a movment of funds from one asset account to antoher to be recorded in expenses for personal budgeting purposes by adding a complementary account to equity.<br>
<br>
With the exception of private stock history JSONs, all JSONs in the data directory are saved and read in Unicode/UTF16.  The application will crash if they are not saved in the correct format.  Edit them through the gui unless you can ensure you will save them correctly.<br>
<br>
Note that quadruple entry trading requires recording appreciation and depreciation of stocks, cryptocurrencies, and commodities.  This is why capital gains and losses are recorded as ghost accounts in single entry.<br>
<br>
It is possible to represent trading activity in this application in accordance with GAAP, but must be done manually for now.<br>
<br>
Tax accounts can be used for anything requiring a single-entry ghost account, it doesn't have to be either directly or indirectly related to taxes.<br>
<br>
Accounts are handled in columns.  To indicate which column an account within a transaction entry belongs in, add the column letter and an exclamation mark.<br>
Example:<br>
D!Cash, C!Gifts<br>
<br>
<br>
<b>ESSENTIAL ELEMENTS</b><br>
Account and Exchange names must be unique, and cannot contain spaces.<br>
Most accounts within the default set of accounts are required, and should probably not be deleted or renamed.<br>
<br>
<b>DATA STRUCTURES</b><br>
LCurrency<br>
{<br>
  "alt": <code>string</code>,<br>
  "flags": <code>string</code>,<br>
  "name": <code>string</code>,<br>
  "places": <code>int</code>,<br>
  "symbol": <code>string</code>,<br>
  "tic": <code>string</code><br>
}<br>
<br>
<u>alt</u>: (cryptocurrencies) the identifier for the cryptocurrency used by Coingecko<br>
<u>flags</u>:<br>
    <u>F</u>: Fiat currency<br>
    <u>D</u>: Currency is defunct and valueless<br>
    <u>T</u>: (Cryptocurrencies) Token, this cryptocurrency is hosted on antoher block chain, not it's own.<br>
    <u>></u>: Currency symbol should be placed at the end of the number not the beginning.<br>
<u>name</u>: The name of the currency.  For cryptocurrencies, if no alt is specified, the name set to lowercase and spaces replaced with hyphens "-" is used.<br>
<u>places</u>: The number of decimal places to use when displaying accounts using this currency<br>
<u>symbol</u>: The symbol placed in front of (or at the end of) a number representing this currency.  It is reccomended that symbols are unique to avoid confusion.<br>
<u>tic</u>: The currency ticker.  For Fiat currencies, thiw should be the ISO 4217 standard ticker for the currency.  In any case, tickers must be unique within a class of currency.  If a specific currency is not supported by Coingecko, the ticker may be needed for it to be supported by other crytpcurrency APIs.<br>
<br>
LStock<br>
{<br>
  "flags": <code>string</code>,<br>
  "name": <code>string</code>,<br>
  "tic": <code>string</code><br>
}<br>
<br>
<u>flags</u>:<br>
    <u>D</u>: This stock is defunct and valueless<br>
    <u>P</u>: This is a private stock<br>
<u>name</u>: The name of the company issuing the stock.<br>
<u>tic</u>: The stock ticker as issued by the root exchange where the stock is traded.<br>
<br>
LInventory<br>
{<br>
  "flags": <code>string</code>,<br>
  "name": <code>string</code>,<br>
  "places": <code>int</code>,<br>
  "symbol": <code>string</code>,<br>
  "tic": <code>string</code><br>
  "value": <code>int</code> {optional}<br>
}<br>
<br>
<u>flags</u>:<br>
    <u>M</u>: This inventory is merchandise<br>
    <u>C</u>: This inventory is a commodity<br>
[Note: these flags are mutually exclusive, as they control how the value of the inventory is calculated.]<br>
<u>name</u>: Name of the inventory<br>
<u>places</u>: The number of decimal places to use when displaying accounts using this currency<br>
<u>symbol</u>: the letters, along with a section sign "§" used to depict the inventory.<br>
<u>tic</u>: for commodities, the commodity ticker.  For other forms of inventory, it should simply be unique.<br>
