# Notes about inventory structure

**Get inventory data**
```
/data get entity BVengo Inventory
```

*Output - I manually added the outer braces and Inventory id for better context*
```json
{
	...,
	Inventory: [
		{
			count: 1,
			Slot: 0b,
			components: {
				"minecraft:container": [
					{
						item: {
							count: 37,
							id: "minecraft:stone"
						},
						slot: 0
					},
					{
						item: {
							count: 12,
							id: "minecraft:acacia_log"
						},
						slot: 1
					}
				]
			},
			id: "minecraft:shulker_box"
		},
		{
			count: 1,
			Slot: 1b,
			components: {
				"minecraft:bundle_contents": [
					{
						count: 3,
						id: "minecraft:torchflower_seeds"
					},
					{
						count: 16,
						id: "minecraft:pumpkin_seeds"
					}
				]
			},
			id: "minecraft:bundle"
		},
		{
			count: 64,
			Slot: 2b,
			id: "minecraft:grass_block"
		}
	],
	...
}
```
