package hu.kristall.rpg.test;

import hu.kristall.rpg.Position;
import hu.kristall.rpg.Server;
import hu.kristall.rpg.WorldsManager;
import hu.kristall.rpg.sync.Synchronizer;
import hu.kristall.rpg.world.World;
import hu.kristall.rpg.world.entity.Entity;
import hu.kristall.rpg.world.entity.EntityType;
import hu.kristall.rpg.world.grid.SearchGrid;
import hu.kristall.rpg.world.path.plan.AStarPathFinder;
import hu.kristall.rpg.world.path.plan.FreePathFinder;
import hu.kristall.rpg.world.path.plan.ReducedPathFinder;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.*;

public class TestWorld {
	
	@Test
	public void testWorldCreation() {
		try {
			Synchronizer<Server> asyncServer = Server.createServer("/", null, 8000);
			Future<Boolean> f = asyncServer.syncCompute(srv -> {
				WorldsManager worldsManager = srv.getWorldsManager();
				Synchronizer<World> asyncWorld = worldsManager.createWorld("w0",1,1,new String[]{""}, new FreePathFinder());
				return worldsManager.getWorld("w0") != null;
			});
			assertTrue(f.get());
			asyncServer.sync(Server::shutdown);
		}
		catch (Throwable e) {
			fail(e);
		}
	}
	
	@Test
	public void testWorldEntitySpawn() {
		try {
			Synchronizer<Server> asyncServer = Server.createServer("/", null, 8001);
			Future<Boolean> f = asyncServer.syncCompute(srv -> {
				WorldsManager worldsManager = srv.getWorldsManager();
				Synchronizer<World> asyncWorld = worldsManager.createWorld("w0",1,1,new String[]{""}, new FreePathFinder());
				try {
					return asyncWorld.syncCompute(world -> {
						int entityCount = world.getEntities().size();
						world.spawnEntity(EntityType.DUMMY, new Position(20,20));
						return world.getEntities().size() == entityCount+1;
					}).get();
				}
				catch (Throwable t) {
					return false;
				}
			});
			assertTrue(f.get());
			asyncServer.sync(Server::shutdown);
		}
		catch (Throwable e) {
			fail(e);
		}
	}
	
	@Test
	public void testWorldEntityTeleport() {
		try {
			Synchronizer<Server> asyncServer = Server.createServer("/", null, 8002);
			Future<Boolean> f = asyncServer.syncCompute(srv -> {
				WorldsManager worldsManager = srv.getWorldsManager();
				Synchronizer<World> asyncWorld = worldsManager.createWorld("w0",1,1,new String[]{""}, new FreePathFinder());
				try {
					return asyncWorld.syncCompute(world -> {
						Entity entity = world.spawnEntity(EntityType.DUMMY, new Position(20,20));
						Position target = new Position(10,10);
						entity.teleport(target);
						return entity.getPosition().equals(target);
					}).get();
				}
				catch (Throwable t) {
					return false;
				}
			});
			assertTrue(f.get());
			asyncServer.sync(Server::shutdown);
		}
		catch (Throwable e) {
			fail(e);
		}
	}
	
	@Test
	public void testWorldEntityWalkFree() {
		try {
			Synchronizer<Server> asyncServer = Server.createServer("/", null, 8003);
			Future<Boolean> f = asyncServer.syncCompute(srv -> {
				WorldsManager worldsManager = srv.getWorldsManager();
				Synchronizer<World> asyncWorld = worldsManager.createWorld("w0",1,1,new String[]{""}, new FreePathFinder());
				try {
					return asyncWorld.syncCompute(world -> {
						Position source = new Position(20,20);
						Entity entity = world.spawnEntity(EntityType.DUMMY, new Position(20,20));
						Position target = new Position(10,10);
						entity.move(target);
						return !entity.getPosition().equals(source);
					}).get();
				}
				catch (Throwable t) {
					t.printStackTrace();
					return false;
				}
			});
			assertTrue(f.get());
			asyncServer.sync(Server::shutdown);
		}
		catch (Throwable e) {
			fail(e);
		}
	}
	
	@Test
	public void testWorldEntityHpChange() {
		try {
			Synchronizer<Server> asyncServer = Server.createServer("/", null, 8004);
			Future<Boolean> f = asyncServer.syncCompute(srv -> {
				WorldsManager worldsManager = srv.getWorldsManager();
				Synchronizer<World> asyncWorld = worldsManager.createWorld("w0",1,1,new String[]{""}, new FreePathFinder());
				try {
					return asyncWorld.syncCompute(world -> {
						Entity entity = world.spawnEntity(EntityType.DUMMY, new Position(20,20));
						double hp = entity.getHp();
						entity.damage(20);
						return Math.round(hp-20 - entity.getHp()) < 0.001;
					}).get();
				}
				catch (Throwable t) {
					t.printStackTrace();
					return false;
				}
			});
			assertTrue(f.get());
			asyncServer.sync(Server::shutdown);
		}
		catch (Throwable e) {
			fail(e);
		}
	}
	
	@Test
	public void testWorldEntityDeath() {
		try {
			Synchronizer<Server> asyncServer = Server.createServer("/", null, 8005);
			Future<Boolean> f = asyncServer.syncCompute(srv -> {
				WorldsManager worldsManager = srv.getWorldsManager();
				Synchronizer<World> asyncWorld = worldsManager.createWorld("w0",1,1,new String[]{""}, new FreePathFinder());
				try {
					return asyncWorld.syncCompute(world -> {
						Entity entity = world.spawnEntity(EntityType.DUMMY, new Position(20,20));
						entity.damage(entity.getHp());
						return entity.isRemoved();
					}).get();
				}
				catch (Throwable t) {
					t.printStackTrace();
					return false;
				}
			});
			assertTrue(f.get());
			asyncServer.sync(Server::shutdown);
		}
		catch (Throwable e) {
			fail(e);
		}
	}
	
	@Test
	public void testWorldReducedPathfinder() {
		ReducedPathFinder reducedPathFinder = new ReducedPathFinder(new Position(0,0), new Position(1,1));
		List<Position> positions = reducedPathFinder.findPath(new Position(-1, -1), new Position(2,2), 1, 0).getCollection();
		assertEquals(2, positions.size());
		assertEquals(new Position(0,0), positions.get(0));
		assertEquals(new Position(1,1), positions.get(1));
		
		Position p0 = new Position(0.5,0.25);
		Position p1 = new Position(0.25, 0.75);
		
		positions = reducedPathFinder.findPath(p0, p1, 1, 0).getCollection();
		assertEquals(2, positions.size());
		assertEquals(p0, positions.get(0));
		assertEquals(p1, positions.get(1));
	}
	
	@Test
	public void testWorldAstartPathfinderSimple() {
		AStarPathFinder finder = new AStarPathFinder(new SearchGrid(new boolean[][]{{false,false}}, 2, 1));
		List<Position> positions = finder.findPath(new Position(0, 0), new Position(1,0), 1, 0).getCollection();
		assertEquals(2, positions.size());
		assertEquals(new Position(0.5,0.5), positions.get(0));
		assertEquals(new Position(1.5,0.5), positions.get(1));
	}
	
	@Test
	public void testWorldAstartPathfinderCorner() {
		AStarPathFinder finder = new AStarPathFinder(new SearchGrid(new boolean[][]{{false,false},{true,false}}, 2, 2));
		List<Position> positions = finder.findPath(new Position(0, 0), new Position(1,1), 1, 0).getCollection();
		assertNotNull(positions);
		assertEquals(3, positions.size());
		assertEquals(new Position(0.5,0.5), positions.get(0));
		assertEquals(new Position(1.5,0.5), positions.get(1));
		assertEquals(new Position(1.5,1.5), positions.get(2));
	}
	
	@Test
	public void testWorldAstartPathfinderRing() {
		AStarPathFinder finder = new AStarPathFinder(new SearchGrid(new boolean[][]{
			{false,false,false},
			{false,true,false},
			{false,false,false},
		}, 3, 3));
		List<Position> positions = finder.findPath(new Position(0, 0), new Position(1,2), 1, 0).getCollection();
		assertNotNull(positions);
		assertEquals(4, positions.size());
		assertEquals(new Position(0.5,0.5), positions.get(0));
		assertEquals(new Position(0.5,1.5), positions.get(1));
		assertEquals(new Position(0.5,2.5), positions.get(2));
		assertEquals(new Position(1.5,2.5), positions.get(3));
	}
	
	
	
	
}
