# BPK Mod (Fabric)
A Fabric mod for Minecraft 26.2 that bedrockify parkour-related things.\
**Requires [Cloth Config](https://modrinth.com/mod/cloth-config)**; install [Mod Menu](https://modrinth.com/mod/modmenu) for the config screen.

**Do NOT use this mod in server *without permission***!

## Feature
- Change some blocks' collision box to bedrock ver. (cactus, chest, ender chest, mob spawner, trapdoor)
- Cancel 45strafe accelerate (`strafeAccelerateAllowed`)
- Hitting wall will not cancel sprint (`beCollisionStopsSprint`)
- Sprint backward (`sprintBackward`)
- Configurable inertia threshold (`inertiaThreshold`, from 0 to 0.005)
- Fixed collision axis order Y->X->Z (`oldCollisionOrder`, vanilla orders X/Z by speed)
- Bedrock slime block bounce (`beSlimeBounce`: the impact tick only records the distance fallen; the reflection and its gravity compensation happen on the next tick)

## Config
Config screen via [Mod Menu](https://modrinth.com/mod/modmenu) (requires [Cloth Config](https://modrinth.com/mod/cloth-config)); config stored at `config/bpk.json`.

## Credit
Cynimal, the pane/bars model overrides are based on his [TrueModels](https://www.curseforge.com/minecraft/texture-packs/truemodels).
