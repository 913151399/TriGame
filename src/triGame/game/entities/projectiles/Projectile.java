package triGame.game.entities.projectiles;

import tSquare.game.entity.Entity;
import tSquare.game.entity.EntityKey;
import tSquare.math.Point;
import triGame.game.ManagerService;
import triGame.game.Params;
import triGame.game.entities.zombies.Zombie;
import triGame.game.entities.buildings.Building;
import triGame.game.shopping.ShopManager;

public class Projectile extends Entity {
	public static final String SPRITE_ID = "projectile";
	private static final int hitBackDistance = 10;
	
	private final ManagerService managers;
	private final ShopManager shop;
	private final boolean noBuildingCollisions;
	
	private int speed, damage;

	protected Projectile(String sSpriteId, double startX, double startY,
			double angle, int speed, int damage, boolean noBuildingCollisions,
			ShopManager shop, ManagerService managers, EntityKey key) {
		
		super(sSpriteId, startX, startY, key);
		this.speed = speed;
		this.damage = damage;
		this.noBuildingCollisions = noBuildingCollisions;
		this.managers = managers;
		this.shop = shop;
		setAngle(angle);
	}

	@Override
	public void performLogic(int frameDelta) {
		moveForward(speed * frameDelta / 1000.0);
		boolean removed = checkBounds();
		
		if (!removed && owned()) {
			if (!checkZombieCollision()) {
				checkBuildingCollision();
			}
		}
	}
	
	protected boolean checkBuildingCollision() {
		if (noBuildingCollisions)
			return false;
		
		Point center = new Point(getCenterX(), getCenterY());
		Params.roundToGrid(center);
		if (!managers.building.objectGrid.isBlockOpen(center)) {
			Building b = collidedWithFirst(managers.building.list);
			if (b != null) {
				handleBuildingCollision(b);
				return true;
			}
		}
		return false;
	}
	
	protected boolean checkZombieCollision() {
		Zombie z = collidedWithFirst(managers.zombie.list);
		if (z != null) {
			handleZombieCollision(z);
			return true;
		}
		return false;
	}
	
	protected void handleBuildingCollision(Building b) {
		b.modifyHealth(damage);
		remove();
	}
	
	protected void handleZombieCollision(Zombie z) {
		z.modifyHealth(damage);
		z.moveForward(-hitBackDistance);
		shop.points++;
		remove();
	}
	
	protected boolean checkBounds() { 
		boolean isOutside = (getX() < 0 || getX() >= Params.GAME_WIDTH ||
				getY() < 0 || getY() >= Params.GAME_HEIGHT);
		
		if (isOutside)
			remove();
		return isOutside;
	}
}
