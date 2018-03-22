import random

class Gamemap( object ):
	NORTH = "North"
	SOUTH = "South"
	EAST  = "East"
	WEST  = "West"
	SURFACE = "Surface"
	
	MINE = "Mine"  # indicates where the player has dropped a mine
	SILENCE = "Silence"  # indicates the player has entered silence mode
	SCENARIO = "Scenario"  # indicates the player has activated the scenario-specific system
	
	DIRS  = [ NORTH, SOUTH, EAST, WEST ]
	
	def __init__( self ):
		self.width = 0
		self.height = 0
		self.map = []
		self.current = MapNode( 0, 0 )
        
        
	def load( self, filename ):
		fd = open( filename, "r" )
		for idx, line in enumerate( fd.readlines() ):
			line = line.strip()
			if idx == 0:
				self.width, self.height = line.split( "x" )
				self.width  = int(self.width)
				self.height = int(self.height)
			else:
				if len(line) != self.width:
					print "Line %d has invalid length.  Found width=%d, width should be %d." % (idx + 1, len(line), self.width)
				else:
					row = []
					for w, cell in enumerate(line):
						node = MapNode( w, idx - 1 )
						if cell != ".":
							node.island = True
						row.append( node )
					self.map.append( row )
		fd.close()
    
	
	def get_node_list( self ):
		nodes = []
		for row in self.map:
			nodes += row
		return nodes
	
        
	def get( self, coord ):
		if coord[ 1 ] < 0 or coord[ 1 ] >= len(self.map):
			return None
		if coord[ 0 ] < 0 or coord[ 0 ] >= len(self.map[ coord[ 1 ] ]):
			return None
		return self.map[ coord[ 1 ] ][ coord[ 0 ] ]
	
	
	def get_in_dir( self, dir ):
		c = self.get_coord_in_dir( dir )
		return self.get( c )
        
        
	def visited( self, coord=None, dir=None ):
		if dir is not None:
			coord = self.get_coord_in_dir( dir )
		node = self.get( coord )
		if node is None:
			return False
		return node.visited
        
        
	def has_mine( self, coord ):
		node = self.get( coord )
		if node is None:
			return False
		return node.mine
        
        
	def get_random_location( self ):
		while True:
			y = random.randint( 0, len(self.map) - 1 )
			x = random.randint( 0, len(self.map[ y ]) - 1 )
 			n = self.get( [ x, y ] )
			if not n.island:
				return n
        
        
	def get_coord_in_dir( self, dir, coord=None ):
		if coord is None:
			coord = [ self.current.x, self.current.y ]
		if dir == self.NORTH:
			return [ coord[ 0 ], coord[ 1 ] - 1 ]
		if dir == self.SOUTH:
			return [ coord[ 0 ], coord[ 1 ] + 1 ]
		if dir == self.WEST:
			return [ coord[ 0 ] - 1, coord[ 1 ] ]
		if dir == self.EAST:
			return [ coord[ 0 ] + 1, coord[ 1 ] ]
		return coord
	
	
	def get_neighbors( self, coord=None ):
		if coord is None:
			coord = [ self.current.x, self.current.y ]
		neighbors = [
			self.get( [coord[ 0 ], coord[ 1 ] - 1 ] ),
			self.get( [coord[ 0 ] + 1, coord[ 1 ] - 1 ] ),
			self.get( [coord[ 0 ] + 1, coord[ 1 ] ] ),
			self.get( [coord[ 0 ] + 1, coord[ 1 ] + 1 ] ),
			self.get( [coord[ 0 ], coord[ 1 ] + 1 ] ),
			self.get( [coord[ 0 ] - 1, coord[ 1 ] + 1 ] ),
			self.get( [coord[ 0 ] - 1, coord[ 1 ] ] ),
			self.get( [coord[ 0 ] - 1, coord[ 1 ] - 1 ] )
		]
		return [ n for n in neighbors if n is not None ]
	
	
	def get_opposite_direction( self, dir ):
		if dir == self.NORTH:
			return self.SOUTH
		if dir == self.SOUTH:
			return self.NORTH
		if dir == self.WEST:
			return self.EAST
		if dir == self.EAST:
			return self.WEST
		return dir
	
	
	def get_sector( self, coord=None ):
		"""Return the sector (zero-based) containing the coord (or self.current if coord is None) """
		if coord is None:
			coord = [ self.current.x, self.current.y ]
		total_rows = len(self.map)
		total_sector_rows = total_rows / 5
		sector_row = coord[ 1 ] / 5
		sector_col = coord[ 0 ] / 5
		return sector_row * total_sector_rows + sector_col
			
			
	def is_in_sector( self, sector, coord=None ):
		if coord is None:
			coord = [ self.current.x, self.current.y ]
		total_rows = len(self.map)
		total_sector_rows = total_rows / 5
		startRow = int(sector / total_sector_rows) * 5      # inclusive
		stopRow  = startRow + 5  # exclusive
		startCol = (sector % total_sector_rows) * 5  # exclusive
		stopCol  = startCol + 5
		return startCol <= coord[ 0 ] < stopCol and startRow <= coord[ 1 ] < stopRow
	
	
	def get_nodes_with_mines( self ):
		return [ n for n in self.get_node_list() if n.mine ]
	
	
	def format_coord( self, coord=None ):
		if coord is None:
			coord = [ self.current.x, self.current.y ]
		y = chr(coord[ 1 ] + ord('A'))
		x += 1
		return "%s%s" % (y, str(x))
	
	
	def print_map( self ):
		for row in self.map:
			r = [ "C" if node == self.current else ("V" if node.visited else ("#" if node.island else ("M" if node.mine else"."))) for node in row ]
			print " ".join( r )
        
        
class MapNode( object ):
	def __init__( self, x, y ):
		self.x = x
		self.y = y
		self.visited = False
		self.mine = False
		self.island = False
        