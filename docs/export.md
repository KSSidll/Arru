# Export Functionality Documentation

This document outlines the export functionality, which provides distinct export structures: Compact CSV, Raw CSV and JSON.

The default selected export structure is Compact CSV, which compresses multiple tables into a single file.

## Considerations

- Choose the export structure based on your specific needs and the tools you plan to use with the exported data.
  - The Raw CSV export maintains table separation, which may be useful for direct database imports or when working with large datasets.
  - The Compact CSV export provides a denormalized view of the data, which can be convenient for data analysis or when a single-file export is preferred.
  - The JSON export provides a denormalized view of the data, which can be convenient for data analysis or when a single-file export is preferred.

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
- `variant.csv`: Contains all data from the ProductVariant table

#### Tables and Their Relationships

##### 1. Category

- Primary Key: `id`
- Relationships:
  - One-to-Many with Product (categoryId)

##### 2. Item

- Primary Key: `id`
- Foreign Keys:
  - `transactionBasketId` references TransactionBasket.id
  - `productId` references Product.id
  - `variantId` references Variant.id (nullable)
- Relationships:
  - Many-to-One with Product
  - Many-to-One with Variant (optional)
  - One-to-Many with Transaction

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
  - Many-to-One with Item

##### 7. Variant

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
id;transactionBasketId;productId;variantId;quantity;price
0;0;0;0;1;4.5
1;0;1;1;1;12
2;1;2;null;0.253;16
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

Example `variant.csv`:

```
id;productId;name
0;0;568 ml
1;1;16 Rolls
```

### 3. JSON

The JSON export combines all data into a single JSON file.

#### File Structure:

- `export.json`: Contains data from all tables

#### Data Format:

Example `export.json`:

```json
[
  {
    "id": 0,
    "date": 1639353600000,
    "cost": 72.57,
    "shop": null,
    "items": [
      {
        "id": 0,
        "price": 4.5,
        "quantity": 1.0,
        "product": {
          "id": 0,
          "name": "Monster",
          "category": {
            "id": 0,
            "name": "Energy Drink"
          },
          "producer": null
        },
        "variant": {
          "id": 0,
          "name": "568 ml"
        }
      },
      {
        "id": 0,
        "price": 12.0,
        "quantity": 1.0,
        "product": {
          "id": 1,
          "name": "Paper",
          "category": {
            "id": 1,
            "name": "Utility"
          },
          "producer": null
        },
        "variant": {
          "id": 1,
          "name": "16 Rolls"
        }
      }
    ]
  }
]
```
