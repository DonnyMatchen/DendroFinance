<h1>DESCRIPTION</h1><br>
This application is designed for management of personal finance and small business finance data.  It is specifically geared towards trading activity, and that portion differs from GAAP in a few ways.<br>
The recommended maximum number of transactions is between 65,000 - 75,000 depending on the commonality of metadata<br>
for help with milliseconds since epoch, this site is an option: <a href="https://www.unixtimestamp.com/">UnixTimeStamp.com</a>
<br>
<h1>COMPATIBILITY</h1><br>
This application requires Java 18 or a later version compatible with it.<br>
Torsocks seems to interfere with this application's ability to read and write files and access APIs.  The latter may be due to many APIs blocking TOR nodes.<br>
In Linux systems using the Nautilus filesystem interface or derivatives, there seems to be a problem when .jar files are run.  It seems to set the working directory to the home folder rather than the directory the .jar file is in.<br>
<br>
With the exception of private stock history JSONs, all JSONs in the data directory are saved and read in Unicode/UTF16.  The application will crash if they are not saved in the correct format.  Edit them through the gui unless you can ensure you will save them correctly.<br>
<br>
<h1>KNOWN PROBLEMS</h1><br>
There is a tendency for some gui windows not to be sized correctly.  I'm not quite sure why this happens, and there doesn't seem to be a fix for it.<br>
<br>
<h1>DOWNLOAD</h1><br>
<a href="https://github.com/DonnyMatchen/DendroFinance/releases">Download</a><br>
<br>
<h1>SINGLE ENTRY, DOUBLE ENTRY, AND QUADRUPLE ENTRY</h1><br>
Tracking accounts and ghost accounts are single entry.<br>
Tracking accounts are used to track the volume of all assets that are not the main currency.  This includes other fiat currencies, stocks, cryptocurrencies, commodities, and other forms of inventory that are not distinguishable.<br>
Ghost accounts are accounts used to track values not appearing on the balance sheet, but worth keeping track of.  The most important example are accounts with tax-relevant totals such as taxable income, capital gains and losses, and deductible expenses.<br>
<br>
Normal accounts use double entry accounting, and adhere largely with GAAP.  For more information on standard accounting practices, see any number of excellent guides on double entry accounting.  An understanding of assets, liabilities, and equity--as well as debits anc credits--is necessary to operate this software.<br><br>
Trading activities use quadruple entry accounting.  This involves the use of an equity account to allow transfers from one asset account to another to be recorded in revenue and expenses.<br>
As an example, if you were buying 1 share of MSFT, this would be represented under double entry as a credit to cash of 200 and a debit to stocks of 200.  Under quadruple accounting, this is represented by the same debit and credit, with an additional debit to Trading_Expenses and credit to an equity account.  This allows for tracking of trading activities in an in depth way.<br>
Another application of quadruple entry accounting is in savings. allowing a movement of funds from one asset account to another to be recorded in expenses for personal budgeting purposes by adding a complementary account to equity.<br>
<br>
Note that quadruple entry trading requires recording appreciation and depreciation of stocks, cryptocurrencies, and commodities.  This is why capital gains and losses are recorded as ghost accounts in single entry.<br>
<br>
It is possible to represent trading activity in this application in accordance with GAAP, but must be done manually for now.<br>
<br>
Accounts are handled in columns.  To indicate which column an account in a transaction entry belongs in, add the column letter and an exclamation mark followed by the account name, then the value in parentheses.<br>
Example:<br>
D!Cash(200), C!Gifts(200)<br>
<br>
<br>
<h1>ESSENTIAL ELEMENTS</h1><br>
Account and Exchange names must be unique, and cannot contain spaces.<br>
Most accounts within the default set of accounts are required, and should probably not be deleted or renamed.<br>
<br>
More specific information can be found under Examples.