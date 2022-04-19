This application is designed for management of personal finance and small business finance data.  It is specifically geared towards trading activity, and that portion differs from GAAP in a few ways.

<b>SINGLE ENTRY, DOUBLE ENTRY, AND QUADRUPLE ENTRY</b>
Tracking accounts and Tax accounts are single entry.
Tracking accounts are used to track the volume of all assets that are not the main currency.  This includes other fiat currencies, stocks, cryptocurrencies, commodities, and other forms of inventory.
Tax accounts are ghost accounts, used to track tax-relevent totals such as taxable income, capital gains and losses, and exemptable expenses.

Normal accounts use double entry accounting, and adhere largely with GAAP.  For more information on standard accounting practices, see any number of excellent guides on double entry accounting.  An understanding of assets, liabilities, and equity--as well as debits anc credits--is necisary to opperate this software.

Trading activities use quadruple entry accounting.  This involves the use of an equity account to allow transfers from one asset account to another to be recorded in revenue and expenses.
As an example, if you were buying 1 share of MSFT, this would be represented under double entry as a credit to cash of 200 and debit to stocks of 200.  Under quadruple accounting, this is represented by the same debit and credit, with an aditional debit to Trading_Expenses and credit to an equity account.  This allows for tracking of trading activities in an in depth way.
Another application of quadruple entry accounting is in savings. allowing a movment of funds from one asset account to antoher to be recorded in expenses for personal budgeting purposes by adding a complementary account to equity.

With the exception of private stock history JSONs, all JSONs in the data directory are saved and read in Unicode/UTF16.  The application will crash if they are not saved in the correct format.  Edit them through the gui unless you can ensure you will save them correctly.

Note that quadruple entry trading requires recording appreciation and depreciation of stocks, cryptocurrencies, and commodities.  This is why capital gains and losses are recorded as ghost accounts in single entry.

It is possible to represent trading activity in this application in accordance with GAAP, but must be done manually for now.

Tax accounts can be used for anything requiring a single-entry ghost account, it doesn't have to be either directly or indirectly related to taxes.

Accounts are handled in columns.  To indicate which column an account within a transaction entry belongs in, add the column letter and an exclamation mark.
Example:
D!Cash, C!Gifts


<b>ESSENTIAL ELEMENTS</b>
Account and Exchange names must be unique, and cannot contain spaces.
Most accounts within the default set of accounts are required, and should probably not be deleted or renamed.

<b>DATA STRUCTURES</b>
LCurrency
{
  "alt": <code>string</code>,
  "flags": <code>string</code>,
  "name": <code>string</code>,
  "places": <code>int</code>,
  "symbol": <code>string</code>,
  "tic": <code>string</code>
},

<u>alt</u>: (cryptocurrencies) the identifier for the cryptocurrency used by Coingecko
<u>flags</u>:
    <u>F</u>: Fiat currency
    <u>D</u>: Currency is defunct and valueless
    <u>T</u>: (Cryptocurrencies) Token, this cryptocurrency is hosted on antoher block chain, not it's own.
    <u>></u>: Currency symbol should be placed at the end of the number not the beginning.
<u>name</u>: The name of the currency.  For cryptocurrencies, if no alt is specified, the name set to lowercase and spaces replaced with hyphens "-" is used.
<u>places</u>: The number of decimal places to use when displaying accounts using this currency
<u>symbol</u>: The symbol placed in front of (or at the end of) a number representing this currency.  It is reccomended that symbols are unique to avoid confusion.
<u>tic</u>: The currency ticker.  For Fiat currencies, thiw should be the ISO 4217 standard ticker for the currency.  In any case, tickers must be unique within a class of currency.  If a specific currency is not supported by Coingecko, the ticker may be needed for it to be supported by other crytpcurrency APIs.

LStock
{
  "flags": <code>string</code>,
  "name": <code>string</code>,
  "tic": <code>string</code>
}

<u>flags</u>:
    <u>D</u>: This stock is defunct and valueless
    <u>P</u>: This is a private stock
<u>name</u>: The name of the company issuing the stock.
<u>tic</u>: The stock ticker as issued by the root exchange where the stock is traded.

LInventory
{
  "flags": <code>string</code>,
  "name": <code>string</code>,
  "places": <code>int</code>,
  "symbol": <code>string</code>,
  "tic": <code>string</code>
  "value": <code>int</code> {optional}
}

<u>flags</u>:
    <u>M</u>: This inventory is merchandise
    <u>C</u>: This inventory is a commodity
[Note: these flags are mutually exclusive, as they control how the value of the inventory is calculated.]
<u>name</u>: Name of the inventory
<u>places</u>: The number of decimal places to use when displaying accounts using this currency
<u>symbol</u>: the letters, along with a section sign "§" used to depict the inventory.
<u>tic</u>: for commodities, the commodity ticker.  For other forms of inventory, it should simply be unique.