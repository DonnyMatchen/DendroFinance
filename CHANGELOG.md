## v0.10.0
* date format improvements and documentation
  * date format input widened and improved
  * new date formats documented
* (X) multi-asset calls to apis
* balance sheet improvements
  * balance sheet importance thresholds
  * difference of accounts
* minor bug fixes
  * cloning transactions now has correct title
  * asset status and position gui now wait to execute potentially lengthy operations

## v0.9.1
* minor bug fixes
  * transaction cloning now functions
  * cleaning moved to its own class
    * cleaning now applied to number validation
    * appreciation and depreciation gui now no longer requires pre-cleaned numbers
  * moved to java 17 for lts and greater compatibility

## v0.9.0
* major updates to guis
  * `NewTransactionEntryGui`
    * removed simple tab, and moved meta fully to it's own tab
      * renamed fields accordingly
    * added an easy way to add ACVs
    * added validation for ACVs
      * with selection
  * `SpecialTransactionEntryGui`
    * transferred from tabs to dynamic radio buttons
    * ensured that data persists across different insert options
* minor updates to guis
  * `MainGui`
    * now alerts user if edit or delete are clicked with no selected transaction
* transaction cloning ant templating
  * `TemplateEntry` created
  * moved transaction buttons to a menu
  * added clone and save as template
  * added `NewTemplateGui`
  * added template selection to `InsertGui`
  * added template editing and deleting
  * import and export templates

## v0.8.4
* minor bug fixes
  * optimized imports
  * minor fixes involving colons
* added enter detection on search fields
  * `AssetStatusGui`
  * `BalanceSheetGui`
  * `CheckGui`
  * `MetaTableGui`
  * `RevExpGui`
* added rate limitation handling to market apis
  * basic implementation
  * automatic wait upon receiving 429
  * gui implementation
  * added to data structures explanations

## v0.8.3
* fixed gui problems
  * moved `pack()` to ensure it is the last thing in every gui's constructor
  * all guis now appear at center of screen
* updated readme

## v0.8.2
* minor optimizations
  * removed unnecessary `& 255` in writing blockSize as a byte
* converted many fields to local in guis
* other optimizations centered around switching to local variables where appropriate

## v0.8.1
* overhaul of encryption
  * moved from ECB to CBC
  * Moved from (AES -> BFL) to (AES)
    * Double encryption doesn't buy much
    * there are no longer trailing identical bytes attached to each block
    * encrypted files are now even more compact
  * changed encrypted file format to something more compact
    * use raw bytes rather than Base64 encoding
    * include block size in file
* moved streams and encryption handler from `fileio` to `fileio.encryption`
* overhaul of file handling
  * More efficient file io used
  * method flow streamlined

## v0.8.0
* minor bug fixes
  * fixed `FileHandler.ensure()`
  * `ImportHandler` now updates `MainGui`
* implemented Jackson library
  * removed janky JSON parsing code
    * removed "raw" string constructors from JsonItems
  * Jackson used for Json input
    * for strings
    * for files
    * for encrypted files
* Json output is now independent of stringification
  * for files
  * for encrypted files
  * handle fatal conditions related to damaged JSONs
* updated `README.md`
* streamlined `FileHandler`, removing redundant methods
* moved `FileHandler`, `ExportHandler`, `ImportHandler` and `EncryptionHandler` from `com.donny.dendrofinance.data` to `com.donny.dendrofinance.fileio`
* added `EncryptionOutputStream` and `DecryptionInputStream`
* changed over to UTF-8 as sole Charset
* added a flag for "large" and block size to `Instance`;

## v0.7.4
* minor bug fixes
  * changed UUIDs to unsigned
  * fixed capital gain/loss disparity checker
  * minor optimizations

## v0.7.3
* minor bug fixes
  * fixed `PositionGui` not handling situations where a currency or asset had no decimal places
  * fixed `DataHandler.buySell` not handling longrun correctly
  * fixed `Instance.reloadBackingElements` mismatched keys with JSON
* updated `Examples`

## v0.7.2
* minor bug fixes
  * fixed columns in `BalanceSheetGui`
  * updated `README.md`
* added manual inventory price history
  * added private inventory
    * updated `LInventory`
    * updated `InventoryEditGui`
    * added "P_Inventory" to `Instance.ensureFolders()`
  * added simplifying code to `Instance.convert()`
    * moved all "getTotal" methods to `Instance.convert()`
    * `Instance.convert()` now supports private stock and inventory

## v0.7.1
* added fee currencies to exchanges
  * added backend
  * added gui tab
  * added fees to exchange edit
* bug fixes
  * `MainGui` buttons made clearer
  * fixed exchange edit and backing edit in general

## v0.7.0
* Minor bug fixes
  * changed Monero (XMR) places from 8 to 12
  * conversion of derivatives to parents
    * added `LCurrency.getRoot()`
    * added root conversion in `DataHandler.minInc()`
  * fixed tracking and ghost account order
    * fixed column order in `MainGui`
    * fixed column order in `TransactionEntry.display()`
    * fixed column order in `AWColumn`;
      * added `LAccountSet.sort()` to `LAccountSet(JsonArray, Instance)`
    * fixed `NewTransactionEntryGui`
      * changed `Instance.getTaxAccountsAsStrings()` to `Instance.getGhostAccountsAsStrings()`
      * fixed old code
  * added folder insure for `Instance.data` in `Instance.ensureFolders()`
  * corrected `Validation.validateJson()`
* added more options to `BalanceSheetGui` search
  * added account currency
  * added account type
  * added broad account type
* added type specific gain/loss
  * specified tax gain/loss accounts in defaults
    * added long run gain/loss accounts to defaults
  * updated `DataHandler` special adder methods
    * added support for specific gain/loss accounts
    * added support for LR vs SR
    * added support for mining income
      * added `DataHandler.minInc()`
      * added mining income support to `SpecialTransactionEntryGui`
* gave more flexibility to accounts in programs
  * fixed `DataHandler`
  * fixed `AssetStatusGui`
  * fixed `AppDepGui`
  * added `special.json`
  * added imports to `Instance`
  * editing a special account edits the special account name
  * added `special.json` to `Examples`
* added budget type list
  * updated `DataHandler.getBudgetTypes()`
  * added `budget.json`
  * removed placeholder account
  * added gui for editing budget types
  * added `budget.json` to `Examples`

## v0.6.3
* updated `README.md`
* `changelog.txt` reformatted and changed to `CHANGELOG.md`
  * changelog revised and corrected
* `Wallet Addresses.txt` reformatted and changed to `WALLET ADDRESSES.md`

## v0.6.2
* fixed `DataHandler.trade()`
* ensured entries in `PositionGui` would be sorted
* minor optimizations and fixes
  * `AccountMetaGui` field `accounts` is now `ACCOUNTS` and final
  * redundant "throws" clause removed from `PositionElement.export()`
  * commented code removed from `ImportHandler`
  * redundant static modifier removed from inner enum `ImportHandler.ImportMode`
  * nine unused imports removed
  * lambda replaced with `Comparator.comparing()` in `DataHandler` and `Position`
  * lambda replaced with method reference in `MainGui`

## v0.6.1
* minor fixes
  * `EncryptionHandler.getKeys()` is now a static method
  * `EncryptionHandler.keysSet()` changed to `EncryptionHandler.keysInitiated()`
  * added more clarity to `ValidationFailedException`
    * added messages to all throws
  * fixed one instance where `Validation.require()` was not used and should have been
  * precision updated for `LMath.cubeRoot()`
  * removed unused class `Partitioner`
  * `Exchange` constructors put in order
  * `JsonArray` now supports construction with ArrayList of objects implementing `ExportableToJson`
  * replaced date with .export() in json exports
* updated `README.md`
* added `Serializable` where appropriate, though exporting to JSON is more compact
* added export to entry totals
  * added export to `OrderBookEntry`
  * added export to `Position`
    * added export to `PositionElement`
* added primitive type constructors for `JsonDecimal`
  * used primitive constructors

## **v0.6.0**
* minor fixes and improvements
  * added `ModalFrame`
  * moved `RegisterFrame` to `gui.customswing`
  * removed anonymous functions
* overhauled password system
  * separated encryption handling from `PasswordGui`
  * implemented logout
  * implemented password change
  * implemented specific password for archives
  * added `UnkPasswordGui`
    * added option for using profile password
* reworked import and export
  * removed export from profile flags
  * added import modes
  * added `.xtbl` option to import
    * password not assumed
    * added support for `.xarc`
  * added `ImportGui`
  * added `ExportGui`

## v0.5.5
* rearranged fields in defaults
* proofread changelog
* replaced "mili" with "milli"
* fixed factor in `currencies.json`

## v0.5.4
* de-generified `Entry` for better memory handling
  * data is now stored as fields rather than in array of generic objects
  * `Entry` made abstract
  * removed `Header` object
  * removed `Field` object
    * it was decided that entries no longer required flexibility of this kind.
* reworked `LType`
  * removed `LString`, `LInt`, `LDecimal`, `LJson`
  * removed `LType` and replaced with interface calls
* removed `ToolBox`
* changed `ExportableToJsonObject` to `ExportableToJson`
* added entry imports to `Examples`
  * added transaction import
    * added metadata
  * added budget import

## v0.5.3
* updated to java 18
* updated `README.md`
* minor bug fixes
* `JsonObject` optimized
* optimized memory usage by replacing `HashMap` with `ArrayList` in `Entry` and `Header`

## v0.5.2
* added check metadata
  * created `CheckMetadata` class
  * added creator and getter metadata methods to `TransactionEntry`
  * added check metadata to metadata tab in `NewTransactionGui`
    * integrated at a basic level
    * option for un-cashed checks added
  * added support for check metadata to `DataHandler`
  * added `CheckGui`
  * added check meta to `TransactionEntry.toString()`
* added meta flags to `MainGui`
* added option to use existing budgets as templates

## v0.5.1
* rationalized alpha
* fixed new/edit transaction gui
  * fixed Meta tab in `NewTransactionGui` not functioning correctly
  * removed "t+" notation
* added `LDate` formatting options
* added date to `PositionGui`
* added artifacts to meta tables

## **v0.5.0**
* minor bug fixes and optimizations
  * replaced calls to `Instance.main.encode()` with `Instance.$()`
  * menus section of main gui made more readable
* removal of extra data files
  * removed `special.json` as it is no longer needed
  * moved contents of `extraneous.json` to `accounts.json`
* reworked budgets
  * restructured `BudgetEntry`
  * reworked budget guis
* reworked loading and saving of entries and backing elements
  * the situation "empty due to deletion" is now handled correctly
  * rationalized `Instance`
  * made reload more extensive and added save
* removed obnoxious trace statements
* added `AccountMetaGui`
* reworked Json
  * reworked `JsonObject`
  * reworked `JsonArray`

## v0.4.2
* fixed date based conversion
* corrected minor typos
* implemented better error printing
* fixed bug in menu
* made `MainGui` more readable

## v0.4.1
* conversion fixed
* Gwei and Wei now have correct names
* fixed title for `NewTransactionEntryGui`
* fixed `ProfileGui`
* fixed `AppDepGui`
* started including more in release files
  * added `src`
  * added `Examples`
  * added `README.md`, `LICENSE.md`, `changelog.txt`, and `wallet addresses.txt`

## **v0.4.0**
* began keeping a permanent changelog
* removed all instances of "amnt", "cst", "xchg", and "cncl" as a variable name and in code
* replaced all instances of `this.getClass()` with `getClass()`
* added loop to ensure File IO works properly for defaults
* changed hang to allow password to be entered to something less processor intense
* overhauled `Instance.convert()`
  * added date to conversion gui
  * added check for "today" in `AssetStatusGui`
* modularized market price APIs
  * added `LMarketApi` and `market-apis.json`
  * added `MarketApiBTC`
  * removed `api-keys` from profile, `ProfileGui`, and `Instance`
  * added check for currency -> main
  * added gui editing for backing elements
  * added data structure to `Examples`
* integrated exchanges more closely with accounts
  * updated account in data structures
  * added exchange to account from gui
* removed stub `MenuItem`
* simplified `EditGui` code