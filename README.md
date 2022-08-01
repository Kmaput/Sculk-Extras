# Sculk Extras
A mod about using sculk to transport your stuff. Created as a Forge Jam 2022 entry.

## Basics
This mod adds 3 new blocks:
- Sculk Jaw: Sneaky member of the sculk family, which bites mobs and eats items that happen to come in contact with it. Also pulls items out of containers.
- Sculk Incubator: Puts eaten item back into the world, container or places them if they are sculk blocks. Also allows for *sculk crafting* and creates Sculk Cocoons when charged by Catalyst.
- Sculk Cocoon: Portable container holding 128 items. It has however some limitations: the inventory cannot be accessed directly by player, instead it has to be broken to retrive the item stored. Machines like hoppers cannot take or insert items into it, the only way is using a Sculk Jaw and Sculk Incubator respectively. It can also store xp, which is inserted by an Incubator. Note it only drops when the cocoon is breaked by a player, like with regular sculk.

## Sculk Item Transport
When an item is eaten by a Sculk Jaw, it can travel through Sculk Blocks. The rules of moving are similar to Sculk Catalyst charge, but items do not turn back to the previous block it moved through or its neighbours. When item reaches Sculk Incubator, it is inserted into container into which it faces, or drops into that space, which also occurs when item has nowhere to move. Note that item's despawn timer is still ticking when beeing trasported and after 30 seconds without finding destination, the item pops back into the world.

## Sculk Tuning
Sculk Jaws and Sculk Incubators can be granted the ability to hold memories by inserting an Echo Shard Fragment into them. After that they can be punished for their actions (or lack thereof) by burning them with flint and steel. This allows to teach then which items to respectively eat and return. A fermented spider eye be used to flip their memories.

## Sculk Crafting
When Sculk Incubator is put besides a soul fire, it gains an ablity to *sculk craft*. When it points into a cocoon with suitable ingredients in appropriate order it may randomly hatch the cocoon into something else. It has recipes to craft functional sculk blocks when provided with sculk or matter convertible into sculk, bones, an ingredient specific to the recipe, some xp and sculk veins, and also recipes to artificially attune echo shard fragments to items or copy their attunement (including sculk memories) to other fragments. The Sculk Incubator will also drop sculk block placed upon it, when it is disrupted by an explosion.

## World Generation
Sculk Jaws can be found everywhere in the Deep Dark. Sculk Incubators generate in Ancient Cities with a cocoon holding some valueable loot.

## Future plans
- Redstone control of Sculk Jaw.
- Advancements guiding the player through the mod
- Tuning Sculk Sensors to only react to specific vibrations, using the same mechanics as for Sculk Jaw and Sculk Incubator. (not the theme of the Jam)
- *A lot of code refactoring*