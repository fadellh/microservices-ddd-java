from pydantic import BaseModel, EmailStr, Field
from typing import List, Optional
from uuid import UUID
from datetime import datetime
from enum import Enum
from bson import ObjectId

# =================================================================================
# ENUMS (Global)
# =================================================================================
class AdminRole(str, Enum):
    SUPER_ADMIN = "SUPER_ADMIN"
    WAREHOUSE_ADMIN = "WAREHOUSE_ADMIN"

class OrderStatus(str, Enum):
    AWAITING_PAYMENT = "AWAITING_PAYMENT"
    REVIEW_PAYMENT = "REVIEW_PAYMENT"
    APPROVED_PENDING = "APPROVED_PENDING"
    APPROVED = "APPROVED"
    CANCEL_PENDING = "CANCEL_PENDING"
    CANCELLED = "CANCELLED"

# -- Example enumeration for StockJournal
class StockJournalReason(str, Enum):
    PURCHASE = "PURCHASE"
    SALE = "SALE"
    RETURN = "RETURN"
    ADJUSTMENT = "ADJUSTMENT"

class StockJournalType(str, Enum):
    IN = "IN"
    OUT = "OUT"

class StockJournalStatus(str, Enum):
    CREATED = "CREATED"
    CONFIRMED = "CONFIRMED"
    CANCELLED = "CANCELLED"

# =================================================================================
# SHARED / BASE MODELS
# =================================================================================
class WarehouseBase(BaseModel):
    id: str #UUID
    name: str
    latitude: float
    longitude: float

class ProductBase(BaseModel):
    id: UUID
    name: str
    description: Optional[str]
    price: float

# =================================================================================
# USER SERVICE (Write DB - Postgres)
# =================================================================================
class AdminUser(BaseModel):
    """
    Admin user data, stored in the write DB (Postgres)
    """
    id: str #UUID
    email: EmailStr
    fullname: str
    adminRole: AdminRole
    active: bool

class CustomerUser(BaseModel):
    """
    Customer user data, stored in the write DB (Postgres)
    """
    id: str #UUID
    email: EmailStr
    fullname: str

# =================================================================================
# ORDER SERVICE
# =================================================================================
# ===================================
# 1) READ SCHEMAS (MongoDB)
# ===================================
# Helper for Mongo's ObjectId
class PyObjectId(ObjectId):
    @classmethod
    def __get_validators__(cls):
        yield cls.validate

    @classmethod
    def validate(cls, v):
        if not ObjectId.is_valid(v):
            raise ValueError(f"Invalid ObjectId: {v}")
        return ObjectId(v)

class ProductDataDoc(BaseModel):
    """

    """
    id: UUID
    name: str
    description: Optional[str]
    price: float

class AdminDataDoc(BaseModel):
    """
    """
    id: UUID
    email: EmailStr
    fullname: str
    adminRole: AdminRole
    active: bool

class CustomerDataDoc(BaseModel):
    """
    """
    id: UUID
    email: EmailStr
    fullname: str

class WarehouseDataDoc(BaseModel):
    """
    """
    id: UUID
    name: str
    latitude: float
    longitude: float

class OrderItemDocument(BaseModel):
    """
    """
    productId: UUID
    productName: str
    price: float
    quantity: int
    subTotal: float

class OrderDetailListDoc(BaseModel):
    """
    """
    id: Optional[PyObjectId] = Field(alias="_id")
    orderId: UUID
    # Basic order info
    orderStatus: OrderStatus
    failureMessages: Optional[str]
    totalAmount: float
    shippingCost: float
    customerAddress: Optional[str]
    # Relationship to other docs
    admin: Optional[AdminDataDoc] = None  # jika ada case admin yang mengelola
    customer: Optional[CustomerDataDoc] = None
    warehouse: Optional[WarehouseDataDoc] = None
    # List item order
    items: List[OrderItemDocument] = []

    # Alamat detail (jika diperlukan)
    address: Optional[str] = None

# ===================================
# 2) WRITE SCHEMAS (Postgres)
# ===================================
class OrderAddressEntity(BaseModel):
    """
    Tabel order_address (Write side - Postgres)
    """
    id: str #UUID
    city: str
    latitude: float
    longitude: float
    postalCode: str
    street: str
    # Relationship 
    orderId: Optional[UUID]

class OrderItemEntity(BaseModel):
    """
    Tabel order_items (Write side - Postgres).
    """
    id: int
    orderId: str #UUID
    productId: str #UUID
    price: float
    quantity: int
    subTotal: float

class OrderEntity(BaseModel):
    """
    Tabel orders (Write side - Postgres)
    """
    id: str #UUID
    customerId: str #UUID
    customerAddress: Optional[str]
    warehouseId: str #UUID
    totalAmount: float
    shippingCost: float
    orderStatus: OrderStatus
    failureMessages: Optional[str]
    address: Optional[OrderAddressEntity]
    items: List[OrderItemEntity] = []

# =================================================================================
# INVENTORY SERVICE
# =================================================================================
# ===================================
# 1) READ SCHEMA (MongoDB)
# ===================================
class CatalogData(BaseModel):
    """
    """
    id: UUID
    name: str
    brand: str
    price: float
    image: Optional[str]
    size: Optional[str]
    availableColors: List[str] = ["red","blue","green","yellow","black","white"]
    maxQuantity: int
    quantity: int = 1

# ===================================
# 2) WRITE SCHEMAS (Postgres)
# ===================================
class StockJournalEntity(BaseModel):
    """
    Tabel stock_journal (Write side - Postgres).
    """
    id: str #UUID
    inventoryItemId: UUID  # relationship to InventoryItemEntity
    totalQuantityChanged: int
    quantityChanged: int
    reason: StockJournalReason
    type: StockJournalType
    status: StockJournalStatus
    createdAt: datetime
    updatedAt: datetime
    deletedAt: Optional[datetime]

class InventoryItem(BaseModel):
    """
    Tabel inventory_items (Write side - Postgres).
    """
    id: str #UUID
    inventoryId: str #UUID
    warehouseId: str #UUID
    quantity: int
    createdAt: datetime
    updatedAt: datetime
    deletedAt: Optional[datetime]
    # Relationship to StockJournal
    stockJournals: List[StockJournalEntity] = []

class InventoryEntity(BaseModel):
    """
    Tabel inventory (Write side - Postgres).
    """
    id: str #UUID
    productId: str #UUID
    totalQuantity: int
    createdAt: datetime
    updatedAt: datetime
    deletedAt: Optional[datetime]
    # Relationship to InventoryItem
    inventoryItems: List[InventoryItem]

class WarehouseEntity(BaseModel):
    """
    Tabel warehouses (Write side - Postgres).
    """
    id: str #UUID
    name: str
    latitude: float
    longitude: float
