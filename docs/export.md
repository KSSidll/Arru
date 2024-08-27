# Export Functionality Documentation

This document outlines the export functionality, which provides two distinct export structures: Compact CSV and Raw CSV.

The default selected export structure is Compact CSV, which compresses multiple tables into a single file.

## Considerations

- Choose the export structure based on your specific needs and the tools you plan to use with the exported data.
  - The Raw CSV export maintains table separation, which may be useful for direct database imports or when working with large datasets.
  - The Compact CSV export provides a denormalized view of the data, which can be convenient for data analysis or when a single-file export is preferred.

## Export Structures

### 1. Compact CSV

The Compact CSV export combines all data into a single CSV file.

#### File Structure:

- `export.csv`: Contains data from all tables

#### Data Format:

The CSV file includes a header row followed by data rows.
The separator used in the format is the semicolon `;`

Example `export.csv`:

```
transactionDate;transactionTotalPrice;shop;product;variant;category;producer;price;quantity
1639353600000;75.57;null;Monster;568 ml;Energy Drink;null;4.5;1
1639353600000;75.57;null;Paper;16 Rolls;Utility;null;12;1
1644451200000;29.11;Aldi;Tomato;null;Vegetables;null;16;0.253
1644451200000;129.37;Allegro;null;null;null;null;null;null
```

### 2. Raw CSV

The Raw CSV export is a literal database dump, with each table exported to a separate CSV file.

#### File Structure:

- `category.csv`: Contains all data from the ProductCategory table
- `item.csv`: Contains all data from the Item table
- `producer.csv`: Contains all data from the ProductProducer table
- `product.csv`: Contains all data from the Product table
- `shop.csv`: Contains all data from the Shop table
- `transaction.csv`: Contains all data from the TransactionBasket table
- `transaction-item.csv`: Contains all data from the TransactionBasketItem table (will be removed in the future in favor of transactionId field in Item table)
- `variant.csv`: Contains all data from the ProductVariant table

#### Tables and Their Relationships

##### 1. Category

- Primary Key: `id`
- Relationships:
  - One-to-Many with Product (categoryId)

##### 2. Item

- Primary Key: `id`
- Foreign Keys:
  - `productId` references Product.id
  - `variantId` references Variant.id (nullable)
- Relationships:
  - Many-to-One with Product
  - Many-to-One with Variant (optional)
  - One-to-Many with Transaction (through TransactionItem, structure allows for Many-to-Many but is actually used as One-to-Many)

##### 3. Producer

- Primary Key: `id`
- Relationships:
  - One-to-Many with Product (producerId)

##### 4. Product

- Primary Key: `id`
- Foreign Keys:
  - `categoryId` references Category.id
  - `producerId` references Producer.id (nullable)
- Relationships:
  - Many-to-One with Category
  - Many-to-One with Producer (optional)
  - One-to-Many with Item
  - One-to-Many with Variant

##### 5. Shop

- Primary Key: `id`
- Relationships:
  - One-to-Many with Transaction (shopId)

##### 6. Transaction

- Primary Key: `id`
- Foreign Key:
  - `shopId` references Shop.id (nullable)
- Relationships:
  - Many-to-One with Shop (optional)
  - Many-to-One with Item (through TransactionItem, structure allows for Many-to-Many but is actually used as Many-to-One)

##### 7. Transaction-Item (Junction Table, will be removed in the future in favor of transactionId field in Item table)

- Primary Key: `id`
- Foreign Keys:
  - `transactionBasketId` references Transaction.id
  - `itemId` references Item.id
- Relationships:
  - Many-to-One with Transaction
  - Many-to-One with Item

##### 8. Variant

- Primary Key: `id`
- Foreign Key:
  - `productId` references Product.id
- Relationships:
  - Many-to-One with Product
  - One-to-Many with Item

#### Notes on Relationships

1. Products are categorized (Category) and may have a producer (Producer).
2. Items represent specific instances of products, potentially with variants.
3. Variants are specific versions or types of products.
4. Transactions occur at shops and consist of multiple items.
5. The TransactionItem table serves as a junction table to create a many-to-many relationship between Transactions and Items. It is planned to be removed in favor of transactionId field in Item table

#### Data Format:

Each CSV file includes a header row followed by data rows.
The separator used in the format is the semicolon `;`

Example `category.csv`:

```
id;name
0;Energy Drink
1;Utility
2;Vegetables
```

Example `item.csv`:

```
id;productId;variantId;quantity;price
0;0;0;1;4.5
1;1;1;1;12
2;2;null;0.253;16
```

Example `producer.csv`:

```
id;name
```

Example `product.csv`:

```
id;categoryId;producerId;name
0;0;null;Monster
1;1;null;Paper
2;2;null;Tomato
```

Example `shop.csv`:

```
id;name
3;Aldi
5;Allegro
```

Example `transaction.csv`:

```
id;date;shopId;totalCost
0;1639353600000;null;75.57
2;1644451200000;3;29.11
7;1644451200000;5;129.37
```

Example `transaction-item.csv`:

```
id;transactionBasketId;itemId
0;0;0
1;0;1
2;1;2
```

Example `variant.csv`:

```
id;productId;name
0;0;568 ml
1;1;16 Rolls
```