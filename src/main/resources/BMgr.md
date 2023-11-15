#### Buffer Interface Functions

这些接口函数将提供一个到文件和访问管理器的接口。所需的函数是：

***FixPage(int page_id, int prot)***

这个函数的原型是FixPage（Page_id，protection），它返回一个frame_id。文件和访问管理器将使用记录的record_id中的page_id调用这个页面。该函数查看页面是否已经在缓冲区中，如果是，则返回相应的frame_id。如果页面尚未驻留在缓冲区中，则它会选择牺牲页面（如果需要），并加载所请求的页面。

***FixNewPage()***

这个函数的原型是FixNewPage（），它返回一个page_id和一个frame_id。当插入、索引拆分或创建对象时需要一个新页面时，使用这个函数。返回page_id以便分配给record_id和元数据。此函数将找到一个空页面，文件和访问管理器可以使用它来存储一些数据。

* ***UnfixPage(int page_id)***

这个函数的原型是UnfixPage（page_id），它返回一个frame_id。这个函数是对FixPage或FixNewPage调用的补充。此函数递减帧上的修复计数。如果计数减少到零，则页面上的闩锁被移除，并且如果选择，则可以移除帧。page_id被转换为frame_id，并且它可以被解锁，使得如果页面上的计数已经减少到零，则它可以被选择为受害页面。

* ***NumFreeFrames()***

NumFreeFrames函数查看缓冲区，并返回可用的缓冲区页数。这对于查询处理器的N路排序特别有用。该函数的原型看起来像NumFreeFrames（），并返回从0到BUFFERSIZE-1（1023）的整数。

***SelectVictim()***

SelectVictim函数选择要替换的帧。如果所选帧的脏位被设置，则需要将页面写入磁盘。

***Hash(int page_id)***

Hash函数将page_id作为参数，并返回帧id。

***RemoveBCB（BCB ptr，int page_id）***

RemoveBCB函数从数组中删除page_id的缓冲区控制块。只有当SelectVictim（）函数需要替换帧时才调用此函数。

***RemoveLRUEle（int frid）***

RemoveLRUEle函数从列表中删除LRU元素。

* ***SetDirty(int frame_id)***

SetDirty函数为frame_id设置脏位。此脏位用于了解是否写出帧。如果内容以任何方式被修改，则必须写入帧。这包括任何目录页和数据页。如果该位为1，则将写入该位。如果该位为零，则不会写入。

* ***UnsetDirty(int frame_id)***

UnsetDirty函数将对应frame_id的dirty_bit分配为零。调用此函数的主要原因是当setDirty（）函数已被调用但页面实际上是临时关系的一部分时。在这种情况下，实际上不需要写入页面，因为它不想被保存。

* ***WriteDirtys()***

系统关闭时必须调用WriteDirtys函数。该函数的目的是写出缓冲区中可能需要写入的任何页。只有当DIRESS_BIT为1时，它才会将页面写出到文件中。

* ***PrintFrame(int frame_id)***

PrintFrame函数打印出由frame_id描述的帧的内容。
